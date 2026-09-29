package com.manacommunity.safety.dto.response;

import com.manacommunity.safety.domain.entities.SecurityAccessEvent;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ControlRoomDashboardResponse {
    private long activeIncidents;
    private long visitorsInside;
    private long vehiclesInside;
    private long deliveriesPending;
    private long unresolvedAlerts;
    private long staffOnDuty;
    private long activeCabsInside;
    private long onlineGatesCount;
    private long offlineGatesCount;
    private List<GateStatusSummary> gates;
    private List<SecurityAccessEvent> liveSecurityEvents;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GateStatusSummary {
        private Long gateId;
        private String gateName;
        private boolean isOnline;
        private int guardsOnDuty;
        private String deviceStatus;
    }
}
