package com.manacommunity.safety.controller;

import com.manacommunity.common.dto.ApiResponse;
import com.manacommunity.safety.domain.entities.HardwareDevice;
import com.manacommunity.safety.dto.request.AnprEventRequest;
import com.manacommunity.safety.dto.request.CctvAlertRequest;
import com.manacommunity.safety.dto.request.RegisterDeviceRequest;
import com.manacommunity.safety.dto.request.RfidScanRequest;
import com.manacommunity.safety.dto.response.HardwareIntegrationResult;
import com.manacommunity.safety.service.IntegrationAdapterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/safety/integrations")
@RequiredArgsConstructor
public class HardwareIntegrationController {

    private final IntegrationAdapterService integrationAdapterService;

    @PostMapping("/devices")
    public ResponseEntity<ApiResponse<HardwareDevice>> registerDevice(@Valid @RequestBody RegisterDeviceRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Device registered", integrationAdapterService.registerDevice(req)));
    }

    @GetMapping("/devices")
    public ResponseEntity<ApiResponse<List<HardwareDevice>>> getDevices(@RequestParam Long communityId) {
        return ResponseEntity.ok(ApiResponse.success("Devices loaded", integrationAdapterService.getDevices(communityId)));
    }

    @PostMapping("/webhook/anpr")
    public ResponseEntity<ApiResponse<HardwareIntegrationResult>> onAnprEvent(@Valid @RequestBody AnprEventRequest req) {
        HardwareIntegrationResult result = integrationAdapterService.processAnprEvent(req);
        return ResponseEntity.ok(ApiResponse.success("ANPR event processed", result));
    }

    @PostMapping("/webhook/rfid")
    public ResponseEntity<ApiResponse<HardwareIntegrationResult>> onRfidScan(@Valid @RequestBody RfidScanRequest req) {
        HardwareIntegrationResult result = integrationAdapterService.processRfidScan(req);
        return ResponseEntity.ok(ApiResponse.success("RFID event processed", result));
    }

    @PostMapping("/webhook/cctv")
    public ResponseEntity<ApiResponse<String>> onCctvAlert(@Valid @RequestBody CctvAlertRequest req) {
        integrationAdapterService.processCctvAlert(req);
        return ResponseEntity.ok(ApiResponse.success("CCTV alert received and dispatched to incident feed", "DISPATCHED"));
    }
}
