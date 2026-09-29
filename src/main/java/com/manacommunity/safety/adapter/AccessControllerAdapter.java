package com.manacommunity.safety.adapter;

import com.manacommunity.safety.domain.enums.DeviceType;
import com.manacommunity.safety.domain.enums.SecurityDecision;
import com.manacommunity.safety.domain.enums.SecurityEventType;
import com.manacommunity.safety.dto.adapter.SecurityDecisionResult;
import com.manacommunity.safety.dto.adapter.SecurityDeviceEvent;
import com.manacommunity.safety.service.SecurityEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccessControllerAdapter implements SecurityDeviceAdapter {

    private final SecurityEventService securityEventService;

    @Override
    public DeviceType getSupportedDeviceType() {
        return DeviceType.SMART_LOCK;
    }

    @Override
    public SecurityDecisionResult processEvent(Long communityId, SecurityDeviceEvent event) {
        String badge = event.getRawData() != null ? event.getRawData().trim() : "";
        boolean authorized = badge.startsWith("ACC-") || badge.length() >= 6;

        securityEventService.recordEvent(communityId, SecurityEventType.ACCESS_GRANTED, "ACCESS_CONTROLLER", null, badge,
                event.getGateId(), "Turnstile / Door Controller", null, null, null, null,
                authorized ? SecurityDecision.ALLOWED : SecurityDecision.DENIED,
                authorized ? "Access controller badge valid" : "Badge invalid", null, "{\"badge\":\"" + badge + "\"}");

        return SecurityDecisionResult.builder()
                .decision(authorized ? SecurityDecision.ALLOWED : SecurityDecision.DENIED)
                .barrierActuated(authorized)
                .matchedEntityType(authorized ? "RESIDENT_BADGE" : "UNKNOWN")
                .message(authorized ? "Access Controller: Door Unlocked." : "Access Controller: Invalid credentials.")
                .build();
    }
}
