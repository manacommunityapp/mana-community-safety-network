package com.manacommunity.safety.service;

import com.manacommunity.safety.domain.entities.*;
import com.manacommunity.safety.domain.enums.*;
import com.manacommunity.safety.dto.request.AnprEventRequest;
import com.manacommunity.safety.dto.request.CctvAlertRequest;
import com.manacommunity.safety.dto.request.RegisterDeviceRequest;
import com.manacommunity.safety.dto.request.RfidScanRequest;
import com.manacommunity.safety.dto.response.HardwareIntegrationResult;
import com.manacommunity.safety.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IntegrationAdapterService {

    private final HardwareDeviceRepository deviceRepository;
    private final HardwareIntegrationLogRepository logRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleAccessLogRepository vehicleAccessLogRepository;
    private final DomesticStaffRepository domesticStaffRepository;
    private final StaffAttendanceLogRepository staffAttendanceLogRepository;
    private final SafetyIncidentRepository incidentRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public HardwareDevice registerDevice(RegisterDeviceRequest req) {
        HardwareDevice device = HardwareDevice.builder()
                .communityId(req.getCommunityId())
                .deviceName(req.getDeviceName())
                .deviceCode(req.getDeviceCode())
                .deviceType(req.getDeviceType())
                .ipAddress(req.getIpAddress())
                .macAddress(req.getMacAddress())
                .gateId(req.getGateId())
                .location(req.getLocation())
                .firmwareVersion(req.getFirmwareVersion())
                .isOnline(true)
                .lastHeartbeatAt(LocalDateTime.now())
                .build();

        return deviceRepository.save(device);
    }

    @Transactional
    public HardwareIntegrationResult processAnprEvent(AnprEventRequest req) {
        long start = System.currentTimeMillis();
        String plate = req.getLicensePlate().toUpperCase().replaceAll("\\s+", "");

        Optional<Vehicle> vehicleOpt = vehicleRepository.findByCommunityIdAndLicensePlateIgnoreCase(req.getCommunityId(), plate);
        boolean isAuthorized = vehicleOpt.isPresent() && Boolean.TRUE.equals(vehicleOpt.get().getIsActive());

        VehicleAccessLog accessLog = VehicleAccessLog.builder()
                .communityId(req.getCommunityId())
                .vehicleId(vehicleOpt.map(Vehicle::getId).orElse(null))
                .licensePlate(plate)
                .direction(req.getDirection() != null ? req.getDirection() : "IN")
                .accessType(vehicleOpt.map(Vehicle::getAccessType).orElse(VehicleAccessType.VISITOR))
                .triggerSource("ANPR")
                .barrierActuated(isAuthorized)
                .isAuthorized(isAuthorized)
                .snapshotPhotoUrl(req.getSnapshotPhotoUrl())
                .build();
        vehicleAccessLogRepository.save(accessLog);

        HardwareIntegrationLog integLog = HardwareIntegrationLog.builder()
                .communityId(req.getCommunityId())
                .deviceType(DeviceType.ANPR_CAMERA)
                .eventType("PLATE_DETECTED")
                .rawPayload("plate=" + plate + ",confidence=" + req.getConfidenceScore())
                .parsedKey(plate)
                .status(isAuthorized ? HardwareEventStatus.PROCESSED : HardwareEventStatus.REJECTED)
                .executionTimeMs(System.currentTimeMillis() - start)
                .responseMessage(isAuthorized ? "Boom barrier opened" : "Unauthorized vehicle")
                .build();
        logRepository.save(integLog);

        messagingTemplate.convertAndSend("/topic/community/" + req.getCommunityId() + "/gate-stream", accessLog);

        return HardwareIntegrationResult.builder()
                .authorized(isAuthorized)
                .barrierActuated(isAuthorized)
                .matchedEntityType(vehicleOpt.isPresent() ? "RESIDENT_VEHICLE" : "UNKNOWN")
                .entityName(vehicleOpt.map(Vehicle::getMakeModel).orElse("Unknown Vehicle"))
                .flatNumber(vehicleOpt.map(Vehicle::getFlatNumber).orElse(null))
                .message(isAuthorized ? "Welcome! Access granted." : "Unregistered vehicle, guard verification required.")
                .build();
    }

    @Transactional
    public HardwareIntegrationResult processRfidScan(RfidScanRequest req) {
        long start = System.currentTimeMillis();
        String tag = req.getRfidTag().trim();

        // 1. Check if vehicle tag
        Optional<Vehicle> vehicleOpt = vehicleRepository.findByCommunityIdAndRfidTagNumber(req.getCommunityId(), tag);
        if (vehicleOpt.isPresent()) {
            Vehicle v = vehicleOpt.get();
            boolean isAuthorized = Boolean.TRUE.equals(v.getIsActive());

            VehicleAccessLog accessLog = VehicleAccessLog.builder()
                    .communityId(req.getCommunityId())
                    .vehicleId(v.getId())
                    .licensePlate(v.getLicensePlate())
                    .rfidTag(tag)
                    .direction(req.getDirection() != null ? req.getDirection() : "IN")
                    .accessType(v.getAccessType())
                    .triggerSource("RFID")
                    .barrierActuated(isAuthorized)
                    .isAuthorized(isAuthorized)
                    .build();
            vehicleAccessLogRepository.save(accessLog);

            return HardwareIntegrationResult.builder()
                    .authorized(isAuthorized)
                    .barrierActuated(isAuthorized)
                    .matchedEntityType("RESIDENT_VEHICLE")
                    .entityName(v.getMakeModel() + " (" + v.getLicensePlate() + ")")
                    .flatNumber(v.getFlatNumber())
                    .message("RFID Validated. Barrier Open.")
                    .build();
        }

        // 2. Check if domestic staff tag
        Optional<DomesticStaff> staffOpt = domesticStaffRepository.findByCommunityIdAndRfidTag(req.getCommunityId(), tag);
        if (staffOpt.isPresent()) {
            DomesticStaff s = staffOpt.get();
            return HardwareIntegrationResult.builder()
                    .authorized(true)
                    .barrierActuated(true)
                    .matchedEntityType("STAFF")
                    .entityName(s.getStaffName())
                    .flatNumber(s.getAssignedFlats())
                    .message("Staff " + s.getStaffName() + " verified.")
                    .build();
        }

        return HardwareIntegrationResult.builder()
                .authorized(false)
                .barrierActuated(false)
                .matchedEntityType("UNKNOWN")
                .message("Unrecognized RFID tag.")
                .build();
    }

    @Transactional
    public void processCctvAlert(CctvAlertRequest req) {
        SafetyIncident incident = SafetyIncident.builder()
                .communityId(req.getCommunityId())
                .incidentNumber("AI-CCTV-" + System.currentTimeMillis())
                .title("AI Camera Alert: " + req.getAlertType())
                .description("Camera " + req.getCameraCode() + " detected " + req.getAlertType() + " at " + req.getLocation())
                .category(IncidentCategory.PERIMETER_BREACH)
                .severity(IncidentSeverity.HIGH)
                .status(IncidentStatus.REPORTED)
                .location(req.getLocation())
                .reportedByUserId(0L)
                .reportedByName("AI CCTV Engine (" + req.getCameraCode() + ")")
                .evidencePhotos(req.getSnapshotUrl())
                .build();

        incidentRepository.save(incident);
        messagingTemplate.convertAndSend("/topic/community/" + req.getCommunityId() + "/incidents", incident);
    }

    @Transactional(readOnly = true)
    public List<HardwareDevice> getDevices(Long communityId) {
        return deviceRepository.findByCommunityId(communityId);
    }
}
