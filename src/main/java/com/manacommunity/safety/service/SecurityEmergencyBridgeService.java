package com.manacommunity.safety.service;

import com.manacommunity.safety.domain.entities.SafetyIncident;
import com.manacommunity.safety.domain.enums.IncidentCategory;
import com.manacommunity.safety.domain.enums.IncidentSeverity;
import com.manacommunity.safety.domain.enums.IncidentStatus;
import com.manacommunity.safety.domain.enums.SecurityDecision;
import com.manacommunity.safety.domain.enums.SecurityEventType;
import com.manacommunity.safety.repository.SafetyIncidentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityEmergencyBridgeService {

    private final SafetyIncidentRepository incidentRepository;
    private final SecurityEventService securityEventService;
    private final SecurityAuditService auditService;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public SafetyIncident triggerGuardPanic(Long communityId, Long guardId, String guardName, Long gateId, String gateName,
                                            String panicType, String location, String description) {
        SafetyIncident incident = SafetyIncident.builder()
                .communityId(communityId)
                .incidentNumber("GUARD-PANIC-" + System.currentTimeMillis())
                .title("🚨 GUARD PANIC TRIGGERED: " + (panicType != null ? panicType : "SECURITY_THREAT"))
                .description(description != null ? description : "Guard " + guardName + " triggered panic alarm at " + gateName)
                .category(IncidentCategory.SUSPICIOUS_ACTIVITY)
                .severity(IncidentSeverity.CRITICAL)
                .status(IncidentStatus.REPORTED)
                .location(location != null ? location : gateName)
                .reportedByUserId(guardId)
                .reportedByName(guardName + " (Gate Security)")
                .build();

        SafetyIncident saved = incidentRepository.save(incident);

        // Record security access/audit event
        securityEventService.recordEvent(communityId, SecurityEventType.PANIC_TRIGGERED, "GUARD", guardId, guardName,
                gateId, gateName, null, null, null, null, SecurityDecision.ESCALATED,
                "Panic alarm triggered: " + panicType, guardId, "{\"panicType\":\"" + panicType + "\"}");

        auditService.recordAction(communityId, "GUARD_PANIC_TRIGGERED", guardId, "GUARD", "INCIDENT", saved.getId(),
                "Critical guard panic triggered at " + gateName, null);

        // Real-time broadcast to Control Room & Emergency channels
        messagingTemplate.convertAndSend("/topic/community/" + communityId + "/emergency-alerts", saved);
        messagingTemplate.convertAndSend("/topic/community/" + communityId + "/incidents", saved);

        log.warn("EMERGENCY ESCALATION: Guard panic triggered for communityId: {}, incidentNumber: {}", communityId, saved.getIncidentNumber());
        return saved;
    }
}
