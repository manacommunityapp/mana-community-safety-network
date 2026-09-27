package com.manacommunity.safety.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DashboardSummaryResponse {
    private Long activeVisitorsInside;
    private Long pendingDeliveries;
    private Long staffOnDuty;
    private Long vehicleEntriesToday;
    private Long vehicleExitsToday;
    private Long activeParkingViolations;
    private Long openIncidents;
    private Long activeGates;
    private List<?> recentGateActivities;
}
