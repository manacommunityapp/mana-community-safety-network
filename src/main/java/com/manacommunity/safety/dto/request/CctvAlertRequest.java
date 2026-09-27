package com.manacommunity.safety.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CctvAlertRequest {
    @NotNull
    private Long communityId;
    private String cameraCode;
    @NotBlank
    private String alertType; // PERIMETER_BREACH, LOITERING, SPEEDING, WRONG_WAY, SMOKE
    private String location;
    private String snapshotUrl;
    private String videoClipUrl;
}
