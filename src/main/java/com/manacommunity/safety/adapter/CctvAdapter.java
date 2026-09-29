package com.manacommunity.safety.adapter;

import com.manacommunity.safety.domain.entities.SafetyIncident;
import com.manacommunity.safety.domain.enums.DeviceType;
import com.manacommunity.safety.domain.enums.IncidentCategory;
import com.manacommunity.safety.domain.enums.IncidentSeverity;
import com.manacommunity.safety.domain.enums.IncidentStatus;
import com.manacommunity.safety.domain.enums.SecurityDecision;
import com.manacommunity.safety.domain.enums.SecurityEventType;
import com.manacommunity.safety.dto.adapter.SecurityDecisionResult;
import com.manacommunity.safety.dto.adapter.SecurityDeviceEvent;
import com.manacommunity.safety.repository.SafetyIncidentRepository;
import com.manacommunity.safety.service.SecurityEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CctvAdapter implements SecurityDeviceAdapter {

    private final SafetyIncidentRepository incidentRepository;
    private final SecurityEventService securityEventService;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public DeviceType getSupportedDeviceType() {
        return DeviceType.CCTV_ANALYTICS;
    }

    @Override
    public SecurityDecisionResult processEvent(Long communityId, SecurityDeviceEvent event) {
        String alertType = event.getRawData() != null ? event.getRawData() : "MOTION_DETECTED";

        SafetyIncident incident = SafetyIncident.builder()
                .communityId(communityId)
                .incidentNumber("AI-CCTV-" + System.currentTimeMillis())
                .title("AI Camera Event: " + alertType)
                .description("Camera " + event.getDeviceCode() + " detected event: " + alertType)
                .category(IncidentCategory.PERIMETER_BREACH)
                .severity(IncidentSeverity.HIGH)
                .status(IncidentStatus.REPORTED)
                .location("Gate " + event.getGateId() + " (" + event.getDeviceCode() + ")")
                .reportedByUserId(0L)
                .reportedByName("AI CCTV Engine (" + event.getDeviceCode() + ")")
                .evidencePhotos(event.getSnapshotPhotoUrl())
                .build();

        SafetyIncident saved = incidentRepository.save(incident);
        messagingTemplate.convertAndSend("/topic/community/" + communityId + "/incidents", saved);

        securityEventService.recordEvent(communityId, SecurityEventType.ANPR_DETECTED, "CCTV", null, event.getDeviceCode(),
                event.getGateId(), "CCTV Perimeter", null, null, null, null, SecurityDecision.ESCALATED,
                "AI CCTV alert: " + alertType, null, "{\"incidentId\":" + saved.getId() + "}");

        return SecurityDecisionResult.builder()
                .decision(SecurityDecision.ESCALATED)
                .barrierActuated(false)
                .matchedEntityType("CCTV_AI_DETECTION")
                .entityName(event.getDeviceCode())
                .message("AI CCTV alert created: " + alertType)
                .alertTriggered(true)
                .alertDetails("Incident " + saved.getIncidentNumber() + " logged")
                .build();
    }
}
