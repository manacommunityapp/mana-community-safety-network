package com.manacommunity.safety.service;

import com.manacommunity.safety.domain.enums.DeliveryStatus;
import com.manacommunity.safety.domain.enums.IncidentStatus;
import com.manacommunity.safety.domain.enums.ViolationStatus;
import com.manacommunity.safety.domain.enums.VisitorStatus;
import com.manacommunity.safety.dto.response.DashboardSummaryResponse;
import com.manacommunity.safety.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final VisitorLogRepository visitorLogRepository;
    private final DeliveryLogRepository deliveryLogRepository;
    private final StaffAttendanceLogRepository staffAttendanceLogRepository;
    private final VehicleAccessLogRepository vehicleAccessLogRepository;
    private final ParkingViolationRepository parkingViolationRepository;
    private final SafetyIncidentRepository safetyIncidentRepository;
    private final GateBoothRepository gateBoothRepository;

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getDashboardSummary(Long communityId) {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();

        Long activeVisitors = visitorLogRepository.countByCommunityIdAndStatus(communityId, VisitorStatus.CHECKED_IN);
        Long pendingDeliveries = deliveryLogRepository.countByCommunityIdAndStatusIn(
                communityId, List.of(DeliveryStatus.ARRIVED_AT_GATE, DeliveryStatus.LEFT_AT_GATE, DeliveryStatus.OUT_FOR_DOOR_DELIVERY));
        Long staffOnDuty = staffAttendanceLogRepository.countByCommunityIdAndCheckOutTimeIsNull(communityId);
        Long vehicleEntries = vehicleAccessLogRepository.countByCommunityIdAndDirectionAndAccessTimeAfter(communityId, "IN", todayStart);
        Long vehicleExits = vehicleAccessLogRepository.countByCommunityIdAndDirectionAndAccessTimeAfter(communityId, "OUT", todayStart);
        Long activeViolations = parkingViolationRepository.countByCommunityIdAndStatusNotIn(
                communityId, List.of(ViolationStatus.PAID, ViolationStatus.WAIVED));
        Long openIncidents = safetyIncidentRepository.countByCommunityIdAndStatusNotIn(
                communityId, List.of(IncidentStatus.RESOLVED, IncidentStatus.CLOSED));
        long activeGates = gateBoothRepository.findByCommunityIdAndIsActiveTrue(communityId).size();

        var recentActivities = vehicleAccessLogRepository.findByCommunityIdOrderByAccessTimeDesc(communityId)
                .stream().limit(10).toList();

        return DashboardSummaryResponse.builder()
                .activeVisitorsInside(activeVisitors)
                .pendingDeliveries(pendingDeliveries)
                .staffOnDuty(staffOnDuty)
                .vehicleEntriesToday(vehicleEntries)
                .vehicleExitsToday(vehicleExits)
                .activeParkingViolations(activeViolations)
                .openIncidents(openIncidents)
                .activeGates(activeGates)
                .recentGateActivities(recentActivities)
                .build();
    }
}
