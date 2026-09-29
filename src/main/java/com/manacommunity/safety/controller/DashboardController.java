package com.manacommunity.safety.controller;

import com.manacommunity.common.dto.ApiResponse;
import com.manacommunity.common.user.security.UserPrincipal;
import com.manacommunity.safety.domain.entities.SafetyIncident;
import com.manacommunity.safety.dto.response.ControlRoomDashboardResponse;
import com.manacommunity.safety.dto.response.DashboardSummaryResponse;
import com.manacommunity.safety.service.DashboardService;
import com.manacommunity.safety.service.SecurityEmergencyBridgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/safety/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final SecurityEmergencyBridgeService emergencyBridgeService;

    @GetMapping("/{communityId}")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getDashboard(@PathVariable Long communityId) {
        DashboardSummaryResponse summary = dashboardService.getDashboardSummary(communityId);
        return ResponseEntity.ok(ApiResponse.success("Dashboard loaded", summary));
    }

    @GetMapping("/{communityId}/control-room")
    public ResponseEntity<ApiResponse<ControlRoomDashboardResponse>> getControlRoomDashboard(@PathVariable Long communityId) {
        ControlRoomDashboardResponse response = dashboardService.getControlRoomDashboard(communityId);
        return ResponseEntity.ok(ApiResponse.success("Control room overview loaded", response));
    }

    @PostMapping("/guard-panic")
    public ResponseEntity<ApiResponse<SafetyIncident>> triggerGuardPanic(
            @RequestParam Long communityId,
            @RequestParam(required = false, defaultValue = "1") Long gateId,
            @RequestParam(required = false, defaultValue = "Main Gate") String gateName,
            @RequestBody(required = false) Map<String, String> body,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long guardId = principal != null ? principal.getId() : 1L;
        String guardName = principal != null && principal.getUser() != null ? principal.getUser().getFullName() : "Gate Guard";
        String panicType = body != null ? body.get("panicType") : "SECURITY_THREAT";
        String location = body != null ? body.get("location") : gateName;
        String description = body != null ? body.get("description") : "Panic trigger actuated by " + guardName;

        SafetyIncident incident = emergencyBridgeService.triggerGuardPanic(
                communityId, guardId, guardName, gateId, gateName, panicType, location, description);
        return ResponseEntity.ok(ApiResponse.success("🚨 GUARD PANIC ESCALATED TO EMERGENCY", incident));
    }
}
