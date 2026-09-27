package com.manacommunity.safety.controller;

import com.manacommunity.common.dto.ApiResponse;
import com.manacommunity.common.user.security.UserPrincipal;
import com.manacommunity.safety.domain.entities.DeliveryLog;
import com.manacommunity.safety.dto.request.CollectDeliveryRequest;
import com.manacommunity.safety.dto.request.LogDeliveryRequest;
import com.manacommunity.safety.service.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/safety/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping("/log")
    public ResponseEntity<ApiResponse<DeliveryLog>> logDeliveryArrival(@Valid @RequestBody LogDeliveryRequest req) {
        DeliveryLog log = deliveryService.logDeliveryArrival(req);
        return ResponseEntity.ok(ApiResponse.success("Delivery recorded", log));
    }

    @PostMapping("/{deliveryId}/collect")
    public ResponseEntity<ApiResponse<DeliveryLog>> collectDelivery(
            @PathVariable Long deliveryId,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CollectDeliveryRequest req) {
        DeliveryLog log = deliveryService.collectDelivery(deliveryId, principal.getId(), req.getCollectionOtp());
        return ResponseEntity.ok(ApiResponse.success("Delivery collected successfully", log));
    }

    @GetMapping("/my-deliveries")
    public ResponseEntity<ApiResponse<List<DeliveryLog>>> getFlatDeliveries(
            @RequestParam Long communityId,
            @RequestParam String flatNumber) {
        return ResponseEntity.ok(ApiResponse.success("Deliveries loaded",
                deliveryService.getFlatDeliveries(communityId, flatNumber)));
    }
}
