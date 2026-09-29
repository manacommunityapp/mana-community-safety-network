package com.manacommunity.safety.adapter;

import com.manacommunity.safety.domain.enums.DeviceType;
import com.manacommunity.safety.dto.adapter.SecurityDecisionResult;
import com.manacommunity.safety.dto.adapter.SecurityDeviceEvent;

public interface SecurityDeviceAdapter {
    DeviceType getSupportedDeviceType();
    SecurityDecisionResult processEvent(Long communityId, SecurityDeviceEvent event);
}
