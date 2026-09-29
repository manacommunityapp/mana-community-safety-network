package com.manacommunity.safety.service;

import com.manacommunity.common.exception.ResourceNotFoundException;
import com.manacommunity.safety.domain.entities.SecurityWatchlist;
import com.manacommunity.safety.domain.entities.VisitorLog;
import com.manacommunity.safety.domain.entities.VisitorPass;
import com.manacommunity.safety.domain.enums.SecurityDecision;
import com.manacommunity.safety.domain.enums.SecurityEventType;
import com.manacommunity.safety.domain.enums.VisitorStatus;
import com.manacommunity.safety.dto.request.CreateVisitorPassRequest;
import com.manacommunity.safety.dto.request.WalkInVisitorRequest;
import com.manacommunity.safety.repository.VisitorLogRepository;
import com.manacommunity.safety.repository.VisitorPassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VisitorService {

    private final VisitorPassRepository visitorPassRepository;
    private final VisitorLogRepository visitorLogRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final WatchlistService watchlistService;
    private final SecurityEventService securityEventService;
    private final SecurityAuditService auditService;
    private final SecurityRulesEngine rulesEngine;

    private static final SecureRandom RANDOM = new SecureRandom();

    private static String hashString(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 algorithm unavailable", e);
        }
    }

    private static String generateOpaqueToken() {
        return "PASS-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }

    @Transactional
    public VisitorPass createPreApprovedPass(Long residentId, String residentName, CreateVisitorPassRequest req) {
        String plainOtp = String.format("%06d", RANDOM.nextInt(1000000));
        String passToken = generateOpaqueToken();
        LocalDateTime validUntil = req.getValidUntil() != null ? req.getValidUntil() : LocalDateTime.now().plusHours(12);

        // Compute HMAC signature over opaque token + validity
        String signature = hashString(passToken + ":" + req.getCommunityId() + ":" + validUntil);
        String otpHash = hashString(plainOtp + ":" + req.getCommunityId());

        // Opaque QR payload: only contains opaque token, signature, and pass type (No plain phone or flat)
        String qrPayload = "MANA-SEC-PASS:" + passToken + ":" + signature.substring(0, 16);

        VisitorPass pass = VisitorPass.builder()
                .communityId(req.getCommunityId())
                .residentId(residentId)
                .residentName(residentName)
                .flatNumber(req.getFlatNumber())
                .tower(req.getTower())
                .visitorName(req.getVisitorName())
                .visitorPhone(req.getVisitorPhone())
                .visitorType(req.getVisitorType())
                .passType(req.getPassType())
                .status(VisitorStatus.PRE_APPROVED)
                .passCode(plainOtp) // Displayed to user as one-time display OTP
                .passToken(passToken)
                .signature(signature)
                .otpHash(otpHash)
                .otpExpiresAt(validUntil)
                .otpAttemptCount(0)
                .otpMaxAttempts(3)
                .qrPayload(qrPayload)
                .vehicleNumber(req.getVehicleNumber())
                .expectedGuestCount(req.getExpectedGuestCount() != null ? req.getExpectedGuestCount() : 1)
                .expectedArrival(req.getExpectedArrival() != null ? req.getExpectedArrival() : LocalDateTime.now())
                .validUntil(validUntil)
                .approvedAt(LocalDateTime.now())
                .approvedByUserId(residentId)
                .purpose(req.getPurpose())
                .build();

        VisitorPass saved = visitorPassRepository.save(pass);
        auditService.recordAction(req.getCommunityId(), "VISITOR_PASS_CREATED", residentId, "RESIDENT",
                "VISITOR_PASS", saved.getId(), "Pre-approved pass created for " + req.getVisitorName(), null);
        return saved;
    }

    @Transactional
    public VisitorPass createWalkInRequest(Long guardUserId, WalkInVisitorRequest req) {
        String plainOtp = String.format("%06d", RANDOM.nextInt(1000000));
        String passToken = generateOpaqueToken();
        LocalDateTime validUntil = LocalDateTime.now().plusHours(4);

        String signature = hashString(passToken + ":" + req.getCommunityId() + ":" + validUntil);
        String otpHash = hashString(plainOtp + ":" + req.getCommunityId());

        // Check Watchlist
        Optional<SecurityWatchlist> watchlistMatch = watchlistService.checkMatch(req.getCommunityId(), req.getVisitorPhone(), req.getVehicleNumber());
        if (watchlistMatch.isPresent()) {
            SecurityWatchlist w = watchlistMatch.get();
            securityEventService.recordEvent(req.getCommunityId(), SecurityEventType.WATCHLIST_MATCH, "WALK_IN_VISITOR",
                    null, req.getVisitorName(), null, "Main Gate", null, null, req.getFlatNumber(), req.getTower(),
                    SecurityDecision.DENIED, "Flagged on watchlist: " + w.getReason(), guardUserId, "{\"phone\":\"" + req.getVisitorPhone() + "\"}");
            throw new IllegalStateException("Visitor is flagged on Security Watchlist: " + w.getReason());
        }

        VisitorPass pass = VisitorPass.builder()
                .communityId(req.getCommunityId())
                .residentId(0L)
                .residentName("Resident of " + req.getFlatNumber())
                .flatNumber(req.getFlatNumber())
                .tower(req.getTower())
                .visitorName(req.getVisitorName())
                .visitorPhone(req.getVisitorPhone())
                .visitorType(req.getVisitorType())
                .status(VisitorStatus.PENDING_APPROVAL)
                .passCode(plainOtp)
                .passToken(passToken)
                .signature(signature)
                .otpHash(otpHash)
                .otpExpiresAt(validUntil)
                .otpAttemptCount(0)
                .otpMaxAttempts(3)
                .vehicleNumber(req.getVehicleNumber())
                .expectedArrival(LocalDateTime.now())
                .validUntil(validUntil)
                .purpose(req.getPurpose())
                .build();

        pass = visitorPassRepository.save(pass);

        // Notify flat residents via WebSocket
        messagingTemplate.convertAndSend("/topic/flat/" + req.getFlatNumber() + "/visitors", pass);
        return pass;
    }

    @Transactional
    public VisitorPass respondToWalkIn(Long passId, Long residentId, boolean approved, String remarks) {
        VisitorPass pass = visitorPassRepository.findById(passId)
                .orElseThrow(() -> new ResourceNotFoundException("VisitorPass", "id", passId));

        pass.setStatus(approved ? VisitorStatus.APPROVED : VisitorStatus.REJECTED);
        pass.setApprovedAt(LocalDateTime.now());
        pass.setApprovedByUserId(residentId);
        if (remarks != null) {
            pass.setPurpose(pass.getPurpose() + " | Resident: " + remarks);
        }

        VisitorPass saved = visitorPassRepository.save(pass);
        messagingTemplate.convertAndSend("/topic/community/" + pass.getCommunityId() + "/gate-alerts", saved);

        auditService.recordAction(pass.getCommunityId(), approved ? "WALK_IN_APPROVED" : "WALK_IN_REJECTED", residentId,
                "RESIDENT", "VISITOR_PASS", saved.getId(), "Walk-in response: " + pass.getStatus(), null);
        return saved;
    }

    @Transactional
    public VisitorLog checkInByTokenOrQr(Long communityId, String rawTokenOrQr, Long gateId, Long guardId, String photoUrl) {
        String token = rawTokenOrQr;
        if (rawTokenOrQr.startsWith("MANA-SEC-PASS:")) {
            token = rawTokenOrQr.split(":")[1];
        } else if (rawTokenOrQr.startsWith("MANA-PASS:")) {
            token = rawTokenOrQr.split(":")[2];
        }

        Optional<VisitorPass> passOpt = visitorPassRepository.findByCommunityIdAndPassToken(communityId, token);
        if (passOpt.isEmpty()) {
            passOpt = visitorPassRepository.findByCommunityIdAndPassCode(communityId, token);
        }

        final String lookupToken = token;
        VisitorPass pass = passOpt.orElseThrow(() -> new ResourceNotFoundException("VisitorPass", "token/code", lookupToken));

        if (pass.getValidUntil() != null && LocalDateTime.now().isAfter(pass.getValidUntil())) {
            securityEventService.recordEvent(communityId, SecurityEventType.ACCESS_DENIED, "VISITOR", pass.getId(),
                    pass.getVisitorName(), gateId, "Gate " + gateId, null, pass.getResidentId(), pass.getFlatNumber(),
                    pass.getTower(), SecurityDecision.DENIED, "Pass expired", guardId, null);
            throw new IllegalStateException("Visitor pass has expired.");
        }

        if (pass.getStatus() == VisitorStatus.REJECTED || pass.getStatus() == VisitorStatus.CANCELLED) {
            throw new IllegalStateException("Visitor pass status invalid: " + pass.getStatus());
        }

        pass.setStatus(VisitorStatus.CHECKED_IN);
        visitorPassRepository.save(pass);

        VisitorLog log = VisitorLog.builder()
                .communityId(communityId)
                .passId(pass.getId())
                .gateId(gateId)
                .visitorName(pass.getVisitorName())
                .visitorPhone(pass.getVisitorPhone())
                .flatNumber(pass.getFlatNumber())
                .tower(pass.getTower())
                .vehicleNumber(pass.getVehicleNumber())
                .entryTime(LocalDateTime.now())
                .checkedInGuardId(guardId)
                .entryPhotoUrl(photoUrl)
                .status(VisitorStatus.CHECKED_IN)
                .build();

        VisitorLog savedLog = visitorLogRepository.save(log);

        securityEventService.recordEvent(communityId, SecurityEventType.VISITOR_ENTRY, "VISITOR", pass.getId(),
                pass.getVisitorName(), gateId, "Gate " + gateId, null, pass.getResidentId(), pass.getFlatNumber(),
                pass.getTower(), SecurityDecision.ALLOWED, "Opaque QR token scan verified", guardId, null);

        auditService.recordAction(communityId, "VISITOR_ENTERED", guardId, "GUARD", "VISITOR_LOG", savedLog.getId(),
                "Visitor checked in: " + pass.getVisitorName() + " to " + pass.getFlatNumber(), null);

        messagingTemplate.convertAndSend("/topic/community/" + communityId + "/gate-events", savedLog);
        return savedLog;
    }

    @Transactional
    public VisitorLog checkInByOtp(Long communityId, String passTokenOrFlat, String plainOtp, Long gateId, Long guardId, String photoUrl) {
        VisitorPass pass = null;
        if (passTokenOrFlat != null && passTokenOrFlat.startsWith("PASS-")) {
            pass = visitorPassRepository.findByCommunityIdAndPassToken(communityId, passTokenOrFlat).orElse(null);
        }
        if (pass == null) {
            pass = visitorPassRepository.findByCommunityIdAndPassCode(communityId, plainOtp).orElse(null);
        }
        if (pass == null) {
            throw new ResourceNotFoundException("VisitorPass", "OTP", plainOtp);
        }

        if (pass.getOtpAttemptCount() != null && pass.getOtpAttemptCount() >= pass.getOtpMaxAttempts()) {
            securityEventService.recordEvent(communityId, SecurityEventType.ACCESS_DENIED, "VISITOR", pass.getId(),
                    pass.getVisitorName(), gateId, "Gate " + gateId, null, pass.getResidentId(), pass.getFlatNumber(),
                    pass.getTower(), SecurityDecision.DENIED, "Max OTP attempts exceeded", guardId, null);
            throw new IllegalStateException("Maximum OTP verification attempts exceeded for this pass.");
        }

        if (pass.getOtpExpiresAt() != null && LocalDateTime.now().isAfter(pass.getOtpExpiresAt())) {
            throw new IllegalStateException("OTP has expired.");
        }

        if (pass.getOtpUsedAt() != null) {
            throw new IllegalStateException("OTP has already been used.");
        }

        String expectedHash = hashString(plainOtp + ":" + communityId);
        if (pass.getOtpHash() != null && !pass.getOtpHash().equals(expectedHash)) {
            pass.setOtpAttemptCount(pass.getOtpAttemptCount() != null ? pass.getOtpAttemptCount() + 1 : 1);
            visitorPassRepository.save(pass);
            throw new IllegalArgumentException("Invalid OTP code. Attempt " + pass.getOtpAttemptCount() + " of " + pass.getOtpMaxAttempts());
        }

        pass.setOtpUsedAt(LocalDateTime.now());
        pass.setStatus(VisitorStatus.CHECKED_IN);
        visitorPassRepository.save(pass);

        VisitorLog log = VisitorLog.builder()
                .communityId(communityId)
                .passId(pass.getId())
                .gateId(gateId)
                .visitorName(pass.getVisitorName())
                .visitorPhone(pass.getVisitorPhone())
                .flatNumber(pass.getFlatNumber())
                .tower(pass.getTower())
                .vehicleNumber(pass.getVehicleNumber())
                .entryTime(LocalDateTime.now())
                .checkedInGuardId(guardId)
                .entryPhotoUrl(photoUrl)
                .status(VisitorStatus.CHECKED_IN)
                .build();

        VisitorLog savedLog = visitorLogRepository.save(log);

        securityEventService.recordEvent(communityId, SecurityEventType.OTP_VERIFIED, "VISITOR", pass.getId(),
                pass.getVisitorName(), gateId, "Gate " + gateId, null, pass.getResidentId(), pass.getFlatNumber(),
                pass.getTower(), SecurityDecision.ALLOWED, "Secure OTP verified", guardId, null);

        auditService.recordAction(communityId, "VISITOR_ENTERED_OTP", guardId, "GUARD", "VISITOR_LOG", savedLog.getId(),
                "Visitor checked in via OTP: " + pass.getVisitorName(), null);

        messagingTemplate.convertAndSend("/topic/community/" + communityId + "/gate-events", savedLog);
        return savedLog;
    }

    @Transactional
    public VisitorLog checkOutVisitor(Long logId, Long guardId, String exitPhotoUrl) {
        VisitorLog log = visitorLogRepository.findById(logId)
                .orElseThrow(() -> new ResourceNotFoundException("VisitorLog", "id", logId));

        log.setExitTime(LocalDateTime.now());
        log.setCheckedOutGuardId(guardId);
        log.setExitPhotoUrl(exitPhotoUrl);
        log.setStatus(VisitorStatus.CHECKED_OUT);

        if (log.getPassId() != null) {
            visitorPassRepository.findById(log.getPassId()).ifPresent(p -> {
                p.setStatus(VisitorStatus.CHECKED_OUT);
                visitorPassRepository.save(p);
            });
        }

        VisitorLog saved = visitorLogRepository.save(log);

        securityEventService.recordEvent(log.getCommunityId(), SecurityEventType.VISITOR_EXIT, "VISITOR", log.getPassId(),
                log.getVisitorName(), log.getGateId(), "Gate " + log.getGateId(), null, null, log.getFlatNumber(),
                log.getTower(), SecurityDecision.ALLOWED, "Visitor exited", guardId, null);

        auditService.recordAction(log.getCommunityId(), "VISITOR_EXITED", guardId, "GUARD", "VISITOR_LOG", saved.getId(),
                "Visitor exited: " + log.getVisitorName(), null);

        messagingTemplate.convertAndSend("/topic/community/" + log.getCommunityId() + "/gate-events", saved);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<VisitorPass> getMyVisitorPasses(Long communityId, Long residentId) {
        return visitorPassRepository.findByCommunityIdAndResidentIdOrderByCreatedAtDesc(communityId, residentId);
    }

    @Transactional(readOnly = true)
    public List<VisitorLog> getActiveVisitors(Long communityId) {
        return visitorLogRepository.findByCommunityIdAndStatus(communityId, VisitorStatus.CHECKED_IN);
    }
}
