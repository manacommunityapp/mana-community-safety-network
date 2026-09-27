package com.manacommunity.safety.service;

import com.manacommunity.safety.dto.request.GateSosTriggerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SafetySosService {

    private final SimpMessagingTemplate messagingTemplate;

    public Map<String, Object> triggerGateSos(Long userId, String userName, GateSosTriggerRequest req) {
        Map<String, Object> payload = Map.of(
                "communityId", req.getCommunityId(),
                "gateId", req.getGateId() != null ? req.getGateId() : 0L,
                "emergencyType", req.getEmergencyType(),
                "description", req.getDescription() != null ? req.getDescription() : "Rapid Gate Emergency",
                "overrideOpenAllBarriers", Boolean.TRUE.equals(req.getOverrideOpenAllBarriers()),
                "triggeredBy", userName,
                "timestamp", System.currentTimeMillis()
        );

        messagingTemplate.convertAndSend("/topic/community/" + req.getCommunityId() + "/sos", payload);
        return payload;
    }
}
