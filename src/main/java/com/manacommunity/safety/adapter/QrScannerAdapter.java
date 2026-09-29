package com.manacommunity.safety.adapter;

import com.manacommunity.safety.domain.entities.VisitorLog;
import com.manacommunity.safety.domain.enums.DeviceType;
import com.manacommunity.safety.domain.enums.SecurityDecision;
import com.manacommunity.safety.dto.adapter.SecurityDecisionResult;
import com.manacommunity.safety.dto.adapter.SecurityDeviceEvent;
import com.manacommunity.safety.service.VisitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class QrScannerAdapter implements SecurityDeviceAdapter {

    private final VisitorService visitorService;

    @Override
    public DeviceType getSupportedDeviceType() {
        return DeviceType.QR_SCANNER;
    }

    @Override
    public SecurityDecisionResult processEvent(Long communityId, SecurityDeviceEvent event) {
        try {
            String token = event.getRawData();
            VisitorLog log = visitorService.checkInByTokenOrQr(communityId, token, event.getGateId(), 0L, event.getSnapshotPhotoUrl());

            return SecurityDecisionResult.builder()
                    .decision(SecurityDecision.ALLOWED)
                    .barrierActuated(true)
                    .matchedEntityType("VISITOR")
                    .entityName(log.getVisitorName())
                    .flatNumber(log.getFlatNumber())
                    .message("QR Pass Validated. Access granted to Flat " + log.getFlatNumber())
                    .build();
        } catch (Exception e) {
            log.warn("QR Scan Adapter rejection: {}", e.getMessage());
            return SecurityDecisionResult.builder()
                    .decision(SecurityDecision.DENIED)
                    .barrierActuated(false)
                    .matchedEntityType("INVALID_PASS")
                    .message("Access Denied: " + e.getMessage())
                    .build();
        }
    }
}
