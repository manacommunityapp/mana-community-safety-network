package com.manacommunity.safety.service;

import com.manacommunity.common.exception.ResourceNotFoundException;
import com.manacommunity.safety.domain.entities.ParkingViolation;
import com.manacommunity.safety.domain.enums.ViolationStatus;
import com.manacommunity.safety.dto.request.ReportParkingViolationRequest;
import com.manacommunity.safety.repository.ParkingViolationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingViolationService {

    private final ParkingViolationRepository parkingViolationRepository;

    @Value("${safety.parking.default-violation-fine:500.00}")
    private BigDecimal defaultFine;

    @Transactional
    public ParkingViolation reportViolation(Long userId, String userName, ReportParkingViolationRequest req) {
        ParkingViolation violation = ParkingViolation.builder()
                .communityId(req.getCommunityId())
                .vehicleNumber(req.getVehicleNumber().toUpperCase().replaceAll("\\s+", ""))
                .parkingSlotNumber(req.getParkingSlotNumber())
                .tower(req.getTower())
                .violationType(req.getViolationType())
                .status(ViolationStatus.REPORTED)
                .description(req.getDescription())
                .photoEvidenceUrl(req.getPhotoEvidenceUrl())
                .fineAmount(req.getFineAmount() != null ? req.getFineAmount() : defaultFine)
                .reportedByUserId(userId)
                .reportedByName(userName)
                .build();

        return parkingViolationRepository.save(violation);
    }

    @Transactional
    public ParkingViolation updateViolationStatus(Long violationId, Long userId, ViolationStatus status, String notes) {
        ParkingViolation v = parkingViolationRepository.findById(violationId)
                .orElseThrow(() -> new ResourceNotFoundException("ParkingViolation", "id", violationId));

        v.setStatus(status);
        if (status == ViolationStatus.PAID || status == ViolationStatus.WAIVED) {
            v.setResolvedByUserId(userId);
            v.setResolvedAt(LocalDateTime.now());
        }
        if (notes != null) {
            v.setResolutionNotes(notes);
        }

        return parkingViolationRepository.save(v);
    }

    @Transactional(readOnly = true)
    public List<ParkingViolation> getViolations(Long communityId, ViolationStatus status) {
        if (status != null) {
            return parkingViolationRepository.findByCommunityIdAndStatus(communityId, status);
        }
        return parkingViolationRepository.findByCommunityIdOrderByReportedAtDesc(communityId);
    }
}
