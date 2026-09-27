package com.manacommunity.safety.controller;

import com.manacommunity.common.dto.ApiResponse;
import com.manacommunity.common.user.security.UserPrincipal;
import com.manacommunity.safety.domain.entities.Vehicle;
import com.manacommunity.safety.domain.entities.VehicleAccessLog;
import com.manacommunity.safety.dto.request.RegisterVehicleRequest;
import com.manacommunity.safety.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/safety/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Vehicle>> registerVehicle(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody RegisterVehicleRequest req) {
        if (req.getResidentId() == null) {
            req.setResidentId(principal.getId());
        }
        Vehicle v = vehicleService.registerVehicle(req);
        return ResponseEntity.ok(ApiResponse.success("Vehicle registered successfully", v));
    }

    @GetMapping("/my-vehicles")
    public ResponseEntity<ApiResponse<List<Vehicle>>> getMyVehicles(
            @RequestParam Long communityId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success("Vehicles loaded",
                vehicleService.getResidentVehicles(communityId, principal.getId())));
    }

    @GetMapping("/logs")
    public ResponseEntity<ApiResponse<List<VehicleAccessLog>>> getRecentLogs(@RequestParam Long communityId) {
        return ResponseEntity.ok(ApiResponse.success("Logs loaded",
                vehicleService.getRecentVehicleLogs(communityId)));
    }
}
