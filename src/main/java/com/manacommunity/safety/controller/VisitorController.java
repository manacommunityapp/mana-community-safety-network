package com.manacommunity.safety.controller;

import com.manacommunity.common.dto.ApiResponse;
import com.manacommunity.common.user.security.UserPrincipal;
import com.manacommunity.safety.domain.entities.VisitorLog;
import com.manacommunity.safety.domain.entities.VisitorPass;
import com.manacommunity.safety.dto.request.CreateVisitorPassRequest;
import com.manacommunity.safety.dto.request.VisitorActionRequest;
import com.manacommunity.safety.dto.request.WalkInVisitorRequest;
import com.manacommunity.safety.service.VisitorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/safety/visitors")
@RequiredArgsConstructor
public class VisitorController {

    private final VisitorService visitorService;

    @PostMapping("/pre-approved")
    public ResponseEntity<ApiResponse<VisitorPass>> createPreApprovedPass(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateVisitorPassRequest req) {
        VisitorPass pass = visitorService.createPreApprovedPass(
                principal.getId(), principal.getUser().getFullName(), req);
        return ResponseEntity.ok(ApiResponse.success("Visitor pass generated", pass));
    }

    @PostMapping("/walk-in")
    public ResponseEntity<ApiResponse<VisitorPass>> createWalkIn(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody WalkInVisitorRequest req) {
        VisitorPass pass = visitorService.createWalkInRequest(principal.getId(), req);
        return ResponseEntity.ok(ApiResponse.success("Walk-in visitor logged and notification sent", pass));
    }

    @PostMapping("/{passId}/respond")
    public ResponseEntity<ApiResponse<VisitorPass>> respondToWalkIn(
            @PathVariable Long passId,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody VisitorActionRequest req) {
        VisitorPass pass = visitorService.respondToWalkIn(passId, principal.getId(), req.getApproved(), req.getRemarks());
        return ResponseEntity.ok(ApiResponse.success("Response recorded", pass));
    }

    @PostMapping("/check-in")
    public ResponseEntity<ApiResponse<VisitorLog>> checkIn(
            @RequestParam Long communityId,
            @RequestParam String code,
            @RequestParam(required = false) Long gateId,
            @RequestParam(required = false) String photoUrl,
            @AuthenticationPrincipal UserPrincipal principal) {
        VisitorLog log = visitorService.checkInVisitor(communityId, code, gateId, principal.getId(), photoUrl);
        return ResponseEntity.ok(ApiResponse.success("Visitor checked in", log));
    }

    @PostMapping("/check-out/{logId}")
    public ResponseEntity<ApiResponse<VisitorLog>> checkOut(
            @PathVariable Long logId,
            @RequestParam(required = false) String exitPhotoUrl,
            @AuthenticationPrincipal UserPrincipal principal) {
        VisitorLog log = visitorService.checkOutVisitor(logId, principal.getId(), exitPhotoUrl);
        return ResponseEntity.ok(ApiResponse.success("Visitor checked out", log));
    }

    @GetMapping("/my-passes")
    public ResponseEntity<ApiResponse<List<VisitorPass>>> getMyPasses(
            @RequestParam Long communityId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success("Passes loaded",
                visitorService.getMyVisitorPasses(communityId, principal.getId())));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<VisitorLog>>> getActiveVisitors(@RequestParam Long communityId) {
        return ResponseEntity.ok(ApiResponse.success("Active visitors loaded",
                visitorService.getActiveVisitors(communityId)));
    }
}
