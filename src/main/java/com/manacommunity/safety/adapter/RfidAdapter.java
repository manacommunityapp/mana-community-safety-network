package com.manacommunity.safety.adapter;

import com.manacommunity.safety.domain.entities.DomesticStaff;
import com.manacommunity.safety.domain.entities.Vehicle;
import com.manacommunity.safety.domain.entities.VehicleAccessLog;
import com.manacommunity.safety.domain.enums.DeviceType;
import com.manacommunity.safety.domain.enums.SecurityDecision;
import com.manacommunity.safety.domain.enums.SecurityEventType;
import com.manacommunity.safety.domain.enums.VehicleAccessType;
import com.manacommunity.safety.dto.adapter.SecurityDecisionResult;
import com.manacommunity.safety.dto.adapter.SecurityDeviceEvent;
import com.manacommunity.safety.repository.DomesticStaffRepository;
import com.manacommunity.safety.repository.VehicleAccessLogRepository;
import com.manacommunity.safety.repository.VehicleRepository;
import com.manacommunity.safety.service.SecurityEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RfidAdapter implements SecurityDeviceAdapter {

    private final VehicleRepository vehicleRepository;
    private final VehicleAccessLogRepository vehicleAccessLogRepository;
    private final DomesticStaffRepository domesticStaffRepository;
    private final SecurityEventService securityEventService;

    @Override
    public DeviceType getSupportedDeviceType() {
        return DeviceType.RFID_READER;
    }

    @Override
    public SecurityDecisionResult processEvent(Long communityId, SecurityDeviceEvent event) {
        String tag = event.getRawData() != null ? event.getRawData().trim() : "";

        // 1. Vehicle RFID tag
        Optional<Vehicle> vehicleOpt = vehicleRepository.findByCommunityIdAndRfidTagNumber(communityId, tag);
        if (vehicleOpt.isPresent()) {
            Vehicle v = vehicleOpt.get();
            boolean isAuthorized = Boolean.TRUE.equals(v.getIsActive());

            VehicleAccessLog accessLog = VehicleAccessLog.builder()
                    .communityId(communityId)
                    .vehicleId(v.getId())
                    .licensePlate(v.getLicensePlate())
                    .rfidTag(tag)
                    .direction(event.getDirection() != null ? event.getDirection() : "IN")
                    .accessType(v.getAccessType() != null ? v.getAccessType() : VehicleAccessType.RESIDENT)
                    .triggerSource("RFID_ADAPTER")
                    .barrierActuated(isAuthorized)
                    .isAuthorized(isAuthorized)
                    .build();
            vehicleAccessLogRepository.save(accessLog);

            securityEventService.recordEvent(communityId, SecurityEventType.RFID_SCANNED, "VEHICLE", v.getId(), v.getLicensePlate(),
                    event.getGateId(), "RFID Gate", null, v.getResidentId(), v.getFlatNumber(), v.getTower(),
                    isAuthorized ? SecurityDecision.ALLOWED : SecurityDecision.DENIED,
                    isAuthorized ? "Valid RFID Tag" : "Inactive Vehicle Tag", null, "{\"tag\":\"" + tag + "\"}");

            return SecurityDecisionResult.builder()
                    .decision(isAuthorized ? SecurityDecision.ALLOWED : SecurityDecision.DENIED)
                    .barrierActuated(isAuthorized)
                    .matchedEntityType("RESIDENT_VEHICLE")
                    .entityName(v.getMakeModel() + " (" + v.getLicensePlate() + ")")
                    .flatNumber(v.getFlatNumber())
                    .message(isAuthorized ? "RFID verified. Barrier actuated." : "Vehicle tag inactive.")
                    .build();
        }

        // 2. Staff RFID badge
        Optional<DomesticStaff> staffOpt = domesticStaffRepository.findByCommunityIdAndRfidTag(communityId, tag);
        if (staffOpt.isPresent()) {
            DomesticStaff s = staffOpt.get();
            boolean active = Boolean.TRUE.equals(s.getIsActive());

            securityEventService.recordEvent(communityId, SecurityEventType.STAFF_ENTRY, "STAFF", s.getId(), s.getStaffName(),
                    event.getGateId(), "Staff RFID Gate", null, null, s.getAssignedFlats(), null,
                    active ? SecurityDecision.ALLOWED : SecurityDecision.DENIED,
                    active ? "Staff RFID Match" : "Inactive Staff Tag", null, "{\"tag\":\"" + tag + "\"}");

            return SecurityDecisionResult.builder()
                    .decision(active ? SecurityDecision.ALLOWED : SecurityDecision.DENIED)
                    .barrierActuated(active)
                    .matchedEntityType("STAFF")
                    .entityName(s.getStaffName() + " (" + s.getStaffType() + ")")
                    .flatNumber(s.getAssignedFlats())
                    .message(active ? "Staff access granted." : "Staff profile inactive.")
                    .build();
        }

        return SecurityDecisionResult.builder()
                .decision(SecurityDecision.DENIED)
                .barrierActuated(false)
                .matchedEntityType("UNKNOWN")
                .message("Unrecognized RFID tag.")
                .build();
    }
}
