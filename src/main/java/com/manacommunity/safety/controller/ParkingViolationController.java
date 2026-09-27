package com.manacommunity.safety.controller;

import com.manacommunity.common.dto.ApiResponse;
import com.manacommunity.common.user.security.UserPrincipal;
import com.manacommunity.safety.domain.entities.ParkingViolation;
import com.manacommunity.safety.domain.enums.ViolationStatus;
import com.manacommunity.safety.dto.request.ReportParkingViolationRequest;
import com.manacommunity.safety.service.ParkingViolationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/safety/parking")
@RequiredArgsConstructor
public class ParkingViolationController {

    private final ParkingViolationService parkingViolationService;

    @PostMapping("/report")
    public ResponseEntity<ApiResponse<ParkingViolation>> reportViolation(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ReportParkingViolationRequest req) {
        ParkingViolation v = parkingViolationService.reportViolation(
                principal.getId(), principal.getUser().getFullName(), req);
        return ResponseEntity.ok(ApiResponse.success("Violation reported", v));
    }

    @PatchMapping("/{violationId}/status")
    public ResponseEntity<ApiResponse<ParkingViolation>> updateStatus(
            @PathVariable Long violationId,
            @RequestParam ViolationStatus status,
            @RequestParam(required = false) String notes,
            @AuthenticationPrincipal UserPrincipal principal) {
        ParkingViolation v = parkingViolationService.updateViolationStatus(violationId, principal.getId(), status, notes);
        return ResponseEntity.ok(ApiResponse.success("Status updated", v));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ParkingViolation>>> getViolations(
            @RequestParam Long communityId,
            @RequestParam(required = false) ViolationStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Violations loaded",
                parkingViolationService.getViolations(communityId, status)));
    }
}
