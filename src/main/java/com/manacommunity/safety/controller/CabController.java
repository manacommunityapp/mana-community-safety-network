package com.manacommunity.safety.controller;

import com.manacommunity.safety.domain.entities.CabLog;
import com.manacommunity.safety.service.CabService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/security/cabs")
@RequiredArgsConstructor
public class CabController {

    private final CabService cabService;

    @GetMapping("/active")
    public ResponseEntity<List<CabLog>> getActiveCabs(
            @RequestHeader(value = "X-Community-Id", defaultValue = "1") Long communityId) {
        return ResponseEntity.ok(cabService.getActiveCabs(communityId));
    }

    @GetMapping
    public ResponseEntity<List<CabLog>> getAllCabLogs(
            @RequestHeader(value = "X-Community-Id", defaultValue = "1") Long communityId) {
        return ResponseEntity.ok(cabService.getAllCabLogs(communityId));
    }

    @PostMapping("/entry")
    public ResponseEntity<CabLog> recordCabEntry(
            @RequestHeader(value = "X-Community-Id", defaultValue = "1") Long communityId,
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long guardId,
            @RequestBody Map<String, String> body) {
        CabLog log = cabService.recordCabEntry(
                communityId,
                body.get("driverName"),
                body.get("cabCompany"),
                body.get("vehicleNumber"),
                body.get("flatNumber"),
                body.get("tower"),
                body.get("passengerResidentId") != null ? Long.parseLong(body.get("passengerResidentId")) : null,
                body.get("passengerName"),
                body.get("pickupType"),
                body.get("gateId") != null ? Long.parseLong(body.get("gateId")) : 1L,
                guardId
        );
        return ResponseEntity.ok(log);
    }

    @PostMapping("/{id}/exit")
    public ResponseEntity<CabLog> recordCabExit(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long guardId) {
        return ResponseEntity.ok(cabService.recordCabExit(id, guardId));
    }
}
