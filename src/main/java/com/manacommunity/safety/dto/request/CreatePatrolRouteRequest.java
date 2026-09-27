package com.manacommunity.safety.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreatePatrolRouteRequest {
    @NotNull
    private Long communityId;
    @NotBlank
    private String routeName;
    private String description;
    private Integer estimatedDurationMinutes;
    private List<CheckpointDto> checkpoints;

    @Data
    public static class CheckpointDto {
        private String checkpointName;
        private String location;
        private String nfcTagId;
        private String qrCode;
        private Integer sequenceOrder;
    }
}
