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
        Long residentId = principal != null ? principal.getId() : 1L;
        String residentName = principal != null && principal.getUser() != null ? principal.getUser().getFullName() : "Resident";
        VisitorPass pass = visitorService.createPreApprovedPass(residentId, residentName, req);
        return ResponseEntity.ok(ApiResponse.success("Secure Opaque Visitor Pass generated", pass));
    }

    @PostMapping("/walk-in")
    public ResponseEntity<ApiResponse<VisitorPass>> createWalkIn(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody WalkInVisitorRequest req) {
        Long guardId = principal != null ? principal.getId() : 1L;
        VisitorPass pass = visitorService.createWalkInRequest(guardId, req);
        return ResponseEntity.ok(ApiResponse.success("Walk-in visitor logged and notification sent", pass));
    }

    @PostMapping("/{passId}/respond")
    public ResponseEntity<ApiResponse<VisitorPass>> respondToWalkIn(
            @PathVariable Long passId,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody VisitorActionRequest req) {
        Long residentId = principal != null ? principal.getId() : 1L;
        VisitorPass pass = visitorService.respondToWalkIn(passId, residentId, req.getApproved(), req.getRemarks());
        return ResponseEntity.ok(ApiResponse.success("Response recorded", pass));
    }

    @PostMapping("/check-in")
    public ResponseEntity<ApiResponse<VisitorLog>> checkIn(
            @RequestParam Long communityId,
            @RequestParam String code,
            @RequestParam(required = false, defaultValue = "1") Long gateId,
            @RequestParam(required = false) String photoUrl,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long guardId = principal != null ? principal.getId() : 1L;
        VisitorLog log = visitorService.checkInByTokenOrQr(communityId, code, gateId, guardId, photoUrl);
        return ResponseEntity.ok(ApiResponse.success("Visitor checked in successfully", log));
    }

    @PostMapping("/check-in-otp")
    public ResponseEntity<ApiResponse<VisitorLog>> checkInOtp(
            @RequestParam Long communityId,
            @RequestParam(required = false) String passToken,
            @RequestParam String otp,
            @RequestParam(required = false, defaultValue = "1") Long gateId,
            @RequestParam(required = false) String photoUrl,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long guardId = principal != null ? principal.getId() : 1L;
        VisitorLog log = visitorService.checkInByOtp(communityId, passToken, otp, gateId, guardId, photoUrl);
        return ResponseEntity.ok(ApiResponse.success("Visitor OTP validated and checked in", log));
    }

    @PostMapping("/check-out/{logId}")
    public ResponseEntity<ApiResponse<VisitorLog>> checkOut(
            @PathVariable Long logId,
            @RequestParam(required = false) String exitPhotoUrl,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long guardId = principal != null ? principal.getId() : 1L;
        VisitorLog log = visitorService.checkOutVisitor(logId, guardId, exitPhotoUrl);
        return ResponseEntity.ok(ApiResponse.success("Visitor checked out", log));
    }

    @GetMapping("/my-passes")
    public ResponseEntity<ApiResponse<List<VisitorPass>>> getMyPasses(
            @RequestParam Long communityId,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long residentId = principal != null ? principal.getId() : 1L;
        return ResponseEntity.ok(ApiResponse.success("Passes loaded",
                visitorService.getMyVisitorPasses(communityId, residentId)));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<VisitorLog>>> getActiveVisitors(@RequestParam Long communityId) {
        return ResponseEntity.ok(ApiResponse.success("Active visitors loaded",
                visitorService.getActiveVisitors(communityId)));
    }
}
