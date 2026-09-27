package com.manacommunity.safety.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnprEventRequest {
    @NotNull
    private Long communityId;
    private String deviceCode;
    @NotBlank
    private String licensePlate;
    private Double confidenceScore;
    private String direction; // IN, OUT
    private String snapshotPhotoUrl;
    private LocalDateTime captureTime;
}
