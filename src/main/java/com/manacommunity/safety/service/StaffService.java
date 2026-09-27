package com.manacommunity.safety.service;

import com.manacommunity.common.exception.ResourceNotFoundException;
import com.manacommunity.safety.domain.entities.DomesticStaff;
import com.manacommunity.safety.domain.entities.StaffAttendanceLog;
import com.manacommunity.safety.domain.enums.VerificationStatus;
import com.manacommunity.safety.dto.request.RegisterStaffRequest;
import com.manacommunity.safety.dto.request.StaffClockInOutRequest;
import com.manacommunity.safety.repository.DomesticStaffRepository;
import com.manacommunity.safety.repository.StaffAttendanceLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final DomesticStaffRepository domesticStaffRepository;
    private final StaffAttendanceLogRepository staffAttendanceLogRepository;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Transactional
    public DomesticStaff registerStaff(RegisterStaffRequest req) {
        String passcode = String.format("%04d", RANDOM.nextInt(10000));

        DomesticStaff staff = DomesticStaff.builder()
                .communityId(req.getCommunityId())
                .staffName(req.getStaffName())
                .phone(req.getPhone())
                .staffType(req.getStaffType())
                .verificationStatus(VerificationStatus.PENDING)
                .govtIdType(req.getGovtIdType())
                .govtIdNumber(req.getGovtIdNumber())
                .photoUrl(req.getPhotoUrl())
                .rfidTag(req.getRfidTag())
                .passcode(passcode)
                .assignedFlats(req.getAssignedFlats())
                .rating(5.0)
                .isActive(true)
                .build();

        return domesticStaffRepository.save(staff);
    }

    @Transactional
    public StaffAttendanceLog handleClockInOut(StaffClockInOutRequest req) {
        DomesticStaff staff = domesticStaffRepository.findById(req.getStaffId())
                .orElseThrow(() -> new ResourceNotFoundException("DomesticStaff", "id", req.getStaffId()));

        Optional<StaffAttendanceLog> activeLog = staffAttendanceLogRepository
                .findFirstByStaffIdAndCheckOutTimeIsNullOrderByCheckInTimeDesc(staff.getId());

        if (activeLog.isPresent()) {
            // Clock out
            StaffAttendanceLog log = activeLog.get();
            log.setCheckOutTime(LocalDateTime.now());
            long minutes = Duration.between(log.getCheckInTime(), log.getCheckOutTime()).toMinutes();
            log.setDurationMinutes(minutes);
            return staffAttendanceLogRepository.save(log);
        } else {
            // Clock in
            StaffAttendanceLog log = StaffAttendanceLog.builder()
                    .communityId(staff.getCommunityId())
                    .staffId(staff.getId())
                    .staffName(staff.getStaffName())
                    .staffType(staff.getStaffType())
                    .gateId(req.getGateId())
                    .checkInTime(LocalDateTime.now())
                    .verificationMethod(req.getVerificationMethod() != null ? req.getVerificationMethod() : "MANUAL")
                    .build();
            return staffAttendanceLogRepository.save(log);
        }
    }

    @Transactional(readOnly = true)
    public List<DomesticStaff> getCommunityStaff(Long communityId) {
        return domesticStaffRepository.findByCommunityIdAndIsActiveTrue(communityId);
    }
}
