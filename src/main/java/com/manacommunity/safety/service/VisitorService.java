package com.manacommunity.safety.service;

import com.manacommunity.common.exception.ResourceNotFoundException;
import com.manacommunity.safety.domain.entities.VisitorLog;
import com.manacommunity.safety.domain.entities.VisitorPass;
import com.manacommunity.safety.domain.enums.VisitorStatus;
import com.manacommunity.safety.dto.request.CreateVisitorPassRequest;
import com.manacommunity.safety.dto.request.WalkInVisitorRequest;
import com.manacommunity.safety.repository.VisitorLogRepository;
import com.manacommunity.safety.repository.VisitorPassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitorService {

    private final VisitorPassRepository visitorPassRepository;
    private final VisitorLogRepository visitorLogRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Transactional
    public VisitorPass createPreApprovedPass(Long residentId, String residentName, CreateVisitorPassRequest req) {
        String passCode = String.format("%06d", RANDOM.nextInt(1000000));
        String qrPayload = "MANA-PASS:" + req.getCommunityId() + ":" + passCode + ":" + req.getFlatNumber();

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
                .passCode(passCode)
                .qrPayload(qrPayload)
                .vehicleNumber(req.getVehicleNumber())
                .expectedGuestCount(req.getExpectedGuestCount() != null ? req.getExpectedGuestCount() : 1)
                .expectedArrival(req.getExpectedArrival() != null ? req.getExpectedArrival() : LocalDateTime.now())
                .validUntil(req.getValidUntil() != null ? req.getValidUntil() : LocalDateTime.now().plusHours(12))
                .approvedAt(LocalDateTime.now())
                .approvedByUserId(residentId)
                .purpose(req.getPurpose())
                .build();

        return visitorPassRepository.save(pass);
    }

    @Transactional
    public VisitorPass createWalkInRequest(Long guardUserId, WalkInVisitorRequest req) {
        String passCode = String.format("%06d", RANDOM.nextInt(1000000));

        VisitorPass pass = VisitorPass.builder()
                .communityId(req.getCommunityId())
                .residentId(0L) // Assigned to flat
                .residentName("Resident of " + req.getFlatNumber())
                .flatNumber(req.getFlatNumber())
                .tower(req.getTower())
                .visitorName(req.getVisitorName())
                .visitorPhone(req.getVisitorPhone())
                .visitorType(req.getVisitorType())
                .status(VisitorStatus.PENDING_APPROVAL)
                .passCode(passCode)
                .vehicleNumber(req.getVehicleNumber())
                .expectedArrival(LocalDateTime.now())
                .validUntil(LocalDateTime.now().plusHours(4))
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
        return saved;
    }

    @Transactional
    public VisitorLog checkInVisitor(Long communityId, String passCodeOrQr, Long gateId, Long guardId, String photoUrl) {
        String code = passCodeOrQr.startsWith("MANA-PASS:") ? passCodeOrQr.split(":")[2] : passCodeOrQr;

        VisitorPass pass = visitorPassRepository.findByCommunityIdAndPassCode(communityId, code)
                .orElseThrow(() -> new ResourceNotFoundException("VisitorPass", "code", code));

        if (pass.getStatus() == VisitorStatus.REJECTED || pass.getStatus() == VisitorStatus.CANCELLED) {
            throw new IllegalStateException("Visitor pass is not valid for entry: " + pass.getStatus());
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
