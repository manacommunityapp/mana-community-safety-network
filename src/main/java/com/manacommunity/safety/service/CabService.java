package com.manacommunity.safety.service;

import com.manacommunity.common.exception.ResourceNotFoundException;
import com.manacommunity.safety.domain.entities.CabLog;
import com.manacommunity.safety.domain.enums.SecurityDecision;
import com.manacommunity.safety.domain.enums.SecurityEventType;
import com.manacommunity.safety.repository.CabLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CabService {

    private final CabLogRepository cabLogRepository;
    private final SecurityEventService securityEventService;
    private final SecurityAuditService auditService;

    @Transactional
    public CabLog recordCabEntry(Long communityId, String driverName, String cabCompany, String vehicleNumber,
                                  String flatNumber, String tower, Long passengerResidentId, String passengerName,
                                  String pickupType, Long gateId, Long guardId) {
        CabLog logEntry = CabLog.builder()
                .communityId(communityId)
                .driverName(driverName)
                .cabCompany(cabCompany != null ? cabCompany : "OTHER")
                .vehicleNumber(vehicleNumber.replaceAll("\\s+", "").toUpperCase())
                .flatNumber(flatNumber)
                .tower(tower)
                .passengerResidentId(passengerResidentId)
                .passengerName(passengerName)
                .pickupType(pickupType != null ? pickupType : "PICKUP")
                .gateId(gateId)
                .guardId(guardId)
                .entryTime(LocalDateTime.now())
                .status("ENTERED")
                .build();

        CabLog saved = cabLogRepository.save(logEntry);

        securityEventService.recordEvent(communityId, SecurityEventType.CAB_ENTRY, "CAB", saved.getId(),
                vehicleNumber, gateId, "Gate " + gateId, null, passengerResidentId, flatNumber, tower,
                SecurityDecision.ALLOWED, "Cab entry registered (" + cabCompany + ")", guardId, null);

        auditService.recordAction(communityId, "CAB_ENTERED", guardId, "GUARD", "CAB_LOG", saved.getId(),
                "Cab entered: " + vehicleNumber + " for Flat " + flatNumber, null);

        return saved;
    }

    @Transactional
    public CabLog recordCabExit(Long logId, Long guardId) {
        CabLog logEntry = cabLogRepository.findById(logId)
                .orElseThrow(() -> new ResourceNotFoundException("CabLog", "id", logId));

        logEntry.setExitTime(LocalDateTime.now());
        logEntry.setStatus("EXITED");
        CabLog saved = cabLogRepository.save(logEntry);

        securityEventService.recordEvent(logEntry.getCommunityId(), SecurityEventType.CAB_EXIT, "CAB", saved.getId(),
                logEntry.getVehicleNumber(), logEntry.getGateId(), "Gate " + logEntry.getGateId(), null,
                logEntry.getPassengerResidentId(), logEntry.getFlatNumber(), logEntry.getTower(),
                SecurityDecision.ALLOWED, "Cab exited", guardId, null);

        auditService.recordAction(logEntry.getCommunityId(), "CAB_EXITED", guardId, "GUARD", "CAB_LOG", saved.getId(),
                "Cab exited: " + logEntry.getVehicleNumber(), null);

        return saved;
    }

    @Transactional(readOnly = true)
    public List<CabLog> getActiveCabs(Long communityId) {
        return cabLogRepository.findByCommunityIdAndStatusOrderByCreatedAtDesc(communityId, "ENTERED");
    }

    @Transactional(readOnly = true)
    public List<CabLog> getAllCabLogs(Long communityId) {
        return cabLogRepository.findByCommunityIdOrderByCreatedAtDesc(communityId);
    }
}
