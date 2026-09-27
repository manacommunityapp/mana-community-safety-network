package com.manacommunity.safety.controller;

import com.manacommunity.common.dto.ApiResponse;
import com.manacommunity.safety.dto.response.DashboardSummaryResponse;
import com.manacommunity.safety.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/safety/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/{communityId}")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getDashboard(@PathVariable Long communityId) {
        DashboardSummaryResponse summary = dashboardService.getDashboardSummary(communityId);
        return ResponseEntity.ok(ApiResponse.success("Dashboard loaded", summary));
    }
}
