package com.manacommunity.safety.controller;

import com.manacommunity.common.dto.ApiResponse;
import com.manacommunity.common.user.security.UserPrincipal;
import com.manacommunity.safety.domain.entities.PatrolLog;
import com.manacommunity.safety.domain.entities.PatrolRoute;
import com.manacommunity.safety.dto.request.CreatePatrolRouteRequest;
import com.manacommunity.safety.dto.request.ScanCheckpointRequest;
import com.manacommunity.safety.service.PatrolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/safety/patrols")
@RequiredArgsConstructor
public class PatrolController {

    private final PatrolService patrolService;

    @PostMapping("/routes")
    public ResponseEntity<ApiResponse<PatrolRoute>> createRoute(@Valid @RequestBody CreatePatrolRouteRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Route created", patrolService.createRoute(req)));
    }

    @PostMapping("/routes/{routeId}/start")
    public ResponseEntity<ApiResponse<PatrolLog>> startPatrol(
            @RequestParam Long communityId,
            @PathVariable Long routeId,
            @AuthenticationPrincipal UserPrincipal principal) {
        PatrolLog log = patrolService.startPatrol(communityId, routeId, principal.getId(), principal.getUser().getFullName());
        return ResponseEntity.ok(ApiResponse.success("Patrol started", log));
    }

    @PostMapping("/checkpoints/scan")
    public ResponseEntity<ApiResponse<PatrolLog>> scanCheckpoint(@Valid @RequestBody ScanCheckpointRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Checkpoint recorded", patrolService.scanCheckpoint(req)));
    }

    @GetMapping("/routes")
    public ResponseEntity<ApiResponse<List<PatrolRoute>>> getRoutes(@RequestParam Long communityId) {
        return ResponseEntity.ok(ApiResponse.success("Routes loaded", patrolService.getRoutes(communityId)));
    }
}
