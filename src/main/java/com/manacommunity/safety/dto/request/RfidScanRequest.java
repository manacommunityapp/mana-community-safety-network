package com.manacommunity.safety.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RfidScanRequest {
    @NotNull
    private Long communityId;
    private String deviceCode;
    @NotBlank
    private String rfidTag;
    private String direction; // IN, OUT
}
