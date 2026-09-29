package com.manacommunity.safety.service;

import com.manacommunity.common.exception.ResourceNotFoundException;
import com.manacommunity.safety.domain.entities.SecurityWatchlist;
import com.manacommunity.safety.domain.enums.WatchlistStatus;
import com.manacommunity.safety.repository.SecurityWatchlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WatchlistService {

    private final SecurityWatchlistRepository watchlistRepository;
    private final SecurityAuditService auditService;

    @Transactional
    public SecurityWatchlist requestWatchlistEntry(Long communityId, Long requestedByUserId, String requestedByName,
                                                  String subjectName, String phone, String licensePlate,
                                                  String idProofNumber, String reason, String auditNotes) {
        SecurityWatchlist entry = SecurityWatchlist.builder()
                .communityId(communityId)
                .subjectName(subjectName)
                .phone(phone != null ? phone.trim() : null)
                .licensePlate(licensePlate != null ? licensePlate.replaceAll("\\s+", "").toUpperCase() : null)
                .idProofNumber(idProofNumber)
                .reason(reason)
                .status(WatchlistStatus.REQUESTED)
                .requestedByUserId(requestedByUserId)
                .requestedByName(requestedByName)
                .auditNotes(auditNotes)
                .build();

        SecurityWatchlist saved = watchlistRepository.save(entry);
        auditService.recordAction(communityId, "WATCHLIST_REQUESTED", requestedByUserId, "USER", "WATCHLIST", saved.getId(),
                "Watchlist entry requested for: " + subjectName + " (" + reason + ")", null);
        return saved;
    }

    @Transactional
    public SecurityWatchlist reviewWatchlistEntry(Long entryId, Long adminUserId, String adminName, boolean approve, String notes) {
        SecurityWatchlist entry = watchlistRepository.findById(entryId)
                .orElseThrow(() -> new ResourceNotFoundException("SecurityWatchlist", "id", entryId));

        entry.setStatus(approve ? WatchlistStatus.APPROVED : WatchlistStatus.REJECTED);
        entry.setApprovedByUserId(adminUserId);
        entry.setApprovedByName(adminName);
        entry.setEffectiveFrom(LocalDateTime.now());
        if (notes != null) {
            entry.setAuditNotes((entry.getAuditNotes() != null ? entry.getAuditNotes() + "\n" : "") + "Review: " + notes);
        }

        SecurityWatchlist saved = watchlistRepository.save(entry);
        auditService.recordAction(entry.getCommunityId(), approve ? "WATCHLIST_APPROVED" : "WATCHLIST_REJECTED", adminUserId, "ADMIN",
                "WATCHLIST", saved.getId(), "Watchlist entry reviewed by " + adminName + ": " + entry.getStatus(), null);
        return saved;
    }

    @Transactional(readOnly = true)
    public Optional<SecurityWatchlist> checkMatch(Long communityId, String phone, String licensePlate) {
        if (phone != null && !phone.isBlank()) {
            Optional<SecurityWatchlist> match = watchlistRepository.findByCommunityIdAndPhoneAndStatus(communityId, phone.trim(), WatchlistStatus.APPROVED);
            if (match.isPresent()) return match;
        }
        if (licensePlate != null && !licensePlate.isBlank()) {
            String plate = licensePlate.replaceAll("\\s+", "").toUpperCase();
            return watchlistRepository.findByCommunityIdAndLicensePlateIgnoreCaseAndStatus(communityId, plate, WatchlistStatus.APPROVED);
        }
        return Optional.empty();
    }

    @Transactional(readOnly = true)
    public List<SecurityWatchlist> getActiveWatchlist(Long communityId) {
        return watchlistRepository.findByCommunityIdAndStatus(communityId, WatchlistStatus.APPROVED);
    }

    @Transactional(readOnly = true)
    public List<SecurityWatchlist> getAllEntries(Long communityId) {
        return watchlistRepository.findByCommunityIdOrderByCreatedAtDesc(communityId);
    }
}
