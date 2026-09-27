package com.manacommunity.safety.controller;

import com.manacommunity.common.dto.ApiResponse;
import com.manacommunity.common.user.security.UserPrincipal;
import com.manacommunity.safety.domain.entities.IncidentUpdate;
import com.manacommunity.safety.domain.entities.SafetyIncident;
import com.manacommunity.safety.dto.request.CreateSafetyIncidentRequest;
import com.manacommunity.safety.dto.request.UpdateIncidentStatusRequest;
import com.manacommunity.safety.service.IncidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/safety/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;

    @PostMapping
    public ResponseEntity<ApiResponse<SafetyIncident>> reportIncident(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateSafetyIncidentRequest req) {
        SafetyIncident incident = incidentService.reportIncident(
                principal.getId(), principal.getUser().getFullName(), req);
        return ResponseEntity.ok(ApiResponse.success("Incident reported", incident));
    }

    @PatchMapping("/{incidentId}/status")
    public ResponseEntity<ApiResponse<SafetyIncident>> updateStatus(
            @PathVariable Long incidentId,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateIncidentStatusRequest req) {
        SafetyIncident incident = incidentService.updateIncidentStatus(
                incidentId, principal.getUser().getFullName(), principal.getUser().getRole(), req);
        return ResponseEntity.ok(ApiResponse.success("Incident updated", incident));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SafetyIncident>>> getIncidents(@RequestParam Long communityId) {
        return ResponseEntity.ok(ApiResponse.success("Incidents loaded", incidentService.getIncidents(communityId)));
    }

    @GetMapping("/{incidentId}/updates")
    public ResponseEntity<ApiResponse<List<IncidentUpdate>>> getUpdates(@PathVariable Long incidentId) {
        return ResponseEntity.ok(ApiResponse.success("Updates loaded", incidentService.getIncidentUpdates(incidentId)));
    }
}
