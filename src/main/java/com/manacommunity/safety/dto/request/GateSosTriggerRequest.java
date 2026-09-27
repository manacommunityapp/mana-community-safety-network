package com.manacommunity.safety.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GateSosTriggerRequest {
    @NotNull
    private Long communityId;
    private Long gateId;
    @NotBlank
    private String emergencyType; // MEDICAL_AMBULANCE, FIRE_TRUCK, POLICE, GATE_BREACH, PANIC
    private String description;
    private Boolean overrideOpenAllBarriers;
}
