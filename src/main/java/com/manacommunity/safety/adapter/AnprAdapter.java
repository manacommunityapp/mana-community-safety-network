package com.manacommunity.safety.adapter;

import com.manacommunity.safety.domain.entities.SecurityWatchlist;
import com.manacommunity.safety.domain.entities.Vehicle;
import com.manacommunity.safety.domain.entities.VehicleAccessLog;
import com.manacommunity.safety.domain.enums.DeviceType;
import com.manacommunity.safety.domain.enums.SecurityDecision;
import com.manacommunity.safety.domain.enums.SecurityEventType;
import com.manacommunity.safety.domain.enums.VehicleAccessType;
import com.manacommunity.safety.dto.adapter.SecurityDecisionResult;
import com.manacommunity.safety.dto.adapter.SecurityDeviceEvent;
import com.manacommunity.safety.repository.VehicleAccessLogRepository;
import com.manacommunity.safety.repository.VehicleRepository;
import com.manacommunity.safety.service.SecurityEventService;
import com.manacommunity.safety.service.WatchlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnprAdapter implements SecurityDeviceAdapter {

    private final VehicleRepository vehicleRepository;
    private final VehicleAccessLogRepository vehicleAccessLogRepository;
    private final WatchlistService watchlistService;
    private final SecurityEventService securityEventService;

    @Override
    public DeviceType getSupportedDeviceType() {
        return DeviceType.ANPR_CAMERA;
    }

    @Override
    public SecurityDecisionResult processEvent(Long communityId, SecurityDeviceEvent event) {
        String plate = event.getRawData() != null ? event.getRawData().toUpperCase().replaceAll("\\s+", "") : "";

        // 1. Check Watchlist match first
        Optional<SecurityWatchlist> watchlistMatch = watchlistService.checkMatch(communityId, null, plate);
        if (watchlistMatch.isPresent()) {
            SecurityWatchlist w = watchlistMatch.get();
            securityEventService.recordEvent(communityId, SecurityEventType.WATCHLIST_MATCH, "VEHICLE", null, plate,
                    event.getGateId(), "ANPR Gate", null, null, null, null, SecurityDecision.DENIED,
                    "Watchlist Blacklist match: " + w.getReason(), null, "{\"plate\":\"" + plate + "\"}");

            return SecurityDecisionResult.builder()
                    .decision(SecurityDecision.DENIED)
                    .barrierActuated(false)
                    .matchedEntityType("WATCHLIST_MATCH")
                    .entityName(w.getSubjectName())
                    .message("🚨 WATCHLIST ALERT: Vehicle flagged on security watchlist. Reason: " + w.getReason())
                    .alertTriggered(true)
                    .alertDetails("Vehicle " + plate + " is on community blacklist")
                    .build();
        }

        // 2. Check Vehicle Registry
        Optional<Vehicle> vehicleOpt = vehicleRepository.findByCommunityIdAndLicensePlateIgnoreCase(communityId, plate);
        boolean isRegistered = vehicleOpt.isPresent() && Boolean.TRUE.equals(vehicleOpt.get().getIsActive());

        VehicleAccessLog accessLog = VehicleAccessLog.builder()
                .communityId(communityId)
                .vehicleId(vehicleOpt.map(Vehicle::getId).orElse(null))
                .licensePlate(plate)
                .direction(event.getDirection() != null ? event.getDirection() : "IN")
                .accessType(vehicleOpt.map(Vehicle::getAccessType).orElse(VehicleAccessType.VISITOR))
                .triggerSource("ANPR_ADAPTER")
                .barrierActuated(isRegistered)
                .isAuthorized(isRegistered)
                .snapshotPhotoUrl(event.getSnapshotPhotoUrl())
                .build();
        vehicleAccessLogRepository.save(accessLog);

        securityEventService.recordEvent(communityId,
                "OUT".equalsIgnoreCase(event.getDirection()) ? SecurityEventType.VEHICLE_EXIT : SecurityEventType.VEHICLE_ENTRY,
                "VEHICLE", vehicleOpt.map(Vehicle::getId).orElse(null), plate, event.getGateId(), "ANPR Lane", null,
                vehicleOpt.map(Vehicle::getResidentId).orElse(null),
                vehicleOpt.map(Vehicle::getFlatNumber).orElse(null),
                vehicleOpt.map(Vehicle::getTower).orElse(null),
                isRegistered ? SecurityDecision.ALLOWED : SecurityDecision.MANUAL_VERIFICATION_REQUIRED,
                isRegistered ? "ANPR Plate Match" : "Unregistered Vehicle", null, "{\"confidence\":" + event.getConfidenceScore() + "}");

        if (isRegistered) {
            Vehicle v = vehicleOpt.get();
            return SecurityDecisionResult.builder()
                    .decision(SecurityDecision.ALLOWED)
                    .barrierActuated(true)
                    .matchedEntityType("RESIDENT_VEHICLE")
                    .entityName(v.getMakeModel() + " (" + plate + ")")
                    .flatNumber(v.getFlatNumber())
                    .message("Welcome home! Barrier opening.")
                    .build();
        } else {
            return SecurityDecisionResult.builder()
                    .decision(SecurityDecision.MANUAL_VERIFICATION_REQUIRED)
                    .barrierActuated(false)
                    .matchedEntityType("UNREGISTERED_VEHICLE")
                    .entityName("Unregistered (" + plate + ")")
                    .message("Unregistered vehicle. Manual guard verification required.")
                    .build();
        }
    }
}
