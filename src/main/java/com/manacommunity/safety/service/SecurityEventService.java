package com.manacommunity.safety.service;

import com.manacommunity.safety.domain.entities.SecurityAccessEvent;
import com.manacommunity.safety.domain.enums.SecurityDecision;
import com.manacommunity.safety.domain.enums.SecurityEventType;
import com.manacommunity.safety.repository.SecurityAccessEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityEventService {

    private final SecurityAccessEventRepository eventRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public SecurityAccessEvent recordEvent(Long communityId, SecurityEventType eventType, String subjectType, Long subjectId,
                                          String subjectIdentifier, Long gateId, String gateName, Long deviceId,
                                          Long residentId, String flatNumber, String tower, SecurityDecision decision,
                                          String decisionReason, Long guardId, String metadataJson) {
        SecurityAccessEvent event = SecurityAccessEvent.builder()
                .communityId(communityId)
                .eventType(eventType)
                .subjectType(subjectType)
                .subjectId(subjectId)
                .subjectIdentifier(subjectIdentifier)
                .gateId(gateId)
                .gateName(gateName)
                .deviceId(deviceId)
                .residentId(residentId)
                .flatNumber(flatNumber)
                .tower(tower)
                .decision(decision)
                .decisionReason(decisionReason)
                .guardId(guardId)
                .metadataJson(metadataJson)
                .build();

        SecurityAccessEvent saved = eventRepository.save(event);
        try {
            messagingTemplate.convertAndSend("/topic/community/" + communityId + "/security-events", saved);
        } catch (Exception e) {
            log.warn("Failed to stream security event via websocket: {}", e.getMessage());
        }
        return saved;
    }

    @Transactional(readOnly = true)
    public List<SecurityAccessEvent> getRecentEvents(Long communityId) {
        return eventRepository.findTop50ByCommunityIdOrderByTimestampDesc(communityId);
    }
}
