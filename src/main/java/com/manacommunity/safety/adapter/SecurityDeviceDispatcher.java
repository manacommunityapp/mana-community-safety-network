package com.manacommunity.safety.adapter;

import com.manacommunity.safety.domain.enums.DeviceType;
import com.manacommunity.safety.dto.adapter.SecurityDecisionResult;
import com.manacommunity.safety.dto.adapter.SecurityDeviceEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityDeviceDispatcher {

    private final List<SecurityDeviceAdapter> adapters;
    private final Map<DeviceType, SecurityDeviceAdapter> adapterRegistry = new EnumMap<>(DeviceType.class);

    @jakarta.annotation.PostConstruct
    public void init() {
        for (SecurityDeviceAdapter adapter : adapters) {
            adapterRegistry.put(adapter.getSupportedDeviceType(), adapter);
        }
        log.info("Initialized SecurityDeviceDispatcher with {} adapters", adapterRegistry.size());
    }

    public SecurityDecisionResult dispatch(Long communityId, SecurityDeviceEvent event) {
        if (event.getDeviceType() == null) {
            throw new IllegalArgumentException("Device type must be specified");
        }

        SecurityDeviceAdapter adapter = adapterRegistry.get(event.getDeviceType());
        if (adapter == null) {
            log.warn("No adapter registered for device type: {}", event.getDeviceType());
            // Fallback for default camera/ANPR or generic
            if (event.getDeviceType() == DeviceType.ANPR_CAMERA) {
                adapter = adapterRegistry.get(DeviceType.ANPR_CAMERA);
            }
        }

        if (adapter == null) {
            throw new UnsupportedOperationException("Unsupported device type: " + event.getDeviceType());
        }

        return adapter.processEvent(communityId, event);
    }
}
