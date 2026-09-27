package com.manacommunity.safety.controller;

import com.manacommunity.common.dto.ApiResponse;
import com.manacommunity.safety.domain.entities.DomesticStaff;
import com.manacommunity.safety.domain.entities.StaffAttendanceLog;
import com.manacommunity.safety.dto.request.RegisterStaffRequest;
import com.manacommunity.safety.dto.request.StaffClockInOutRequest;
import com.manacommunity.safety.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/safety/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<DomesticStaff>> registerStaff(@Valid @RequestBody RegisterStaffRequest req) {
        DomesticStaff staff = staffService.registerStaff(req);
        return ResponseEntity.ok(ApiResponse.success("Staff member registered", staff));
    }

    @PostMapping("/clock")
    public ResponseEntity<ApiResponse<StaffAttendanceLog>> handleClockInOut(@Valid @RequestBody StaffClockInOutRequest req) {
        StaffAttendanceLog log = staffService.handleClockInOut(req);
        return ResponseEntity.ok(ApiResponse.success("Attendance updated", log));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DomesticStaff>>> getStaff(@RequestParam Long communityId) {
        return ResponseEntity.ok(ApiResponse.success("Staff loaded", staffService.getCommunityStaff(communityId)));
    }
}
