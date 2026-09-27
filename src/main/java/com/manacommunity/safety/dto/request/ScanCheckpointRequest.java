package com.manacommunity.safety.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScanCheckpointRequest {
    @NotNull
    private Long patrolLogId;
    @NotNull
    private Long checkpointId;
    private String tagOrCodeScanned;
}
