package com.manacommunity.safety.controller;

import com.manacommunity.common.dto.ApiResponse;
import com.manacommunity.common.user.security.UserPrincipal;
import com.manacommunity.safety.dto.request.GateSosTriggerRequest;
import com.manacommunity.safety.service.SafetySosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/safety/sos")
@RequiredArgsConstructor
public class SafetySosController {

    private final SafetySosService safetySosService;

    @PostMapping("/trigger")
    public ResponseEntity<ApiResponse<Map<String, Object>>> triggerGateSos(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody GateSosTriggerRequest req) {
        var res = safetySosService.triggerGateSos(principal.getId(), principal.getUser().getFullName(), req);
        return ResponseEntity.ok(ApiResponse.success("Gate SOS Broadcast Activated", res));
    }
}
