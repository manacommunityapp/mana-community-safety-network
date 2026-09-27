package com.manacommunity.safety.dto.request;

import com.manacommunity.safety.domain.enums.VisitorType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WalkInVisitorRequest {
    @NotNull
    private Long communityId;
    @NotBlank
    private String flatNumber;
    private String tower;
    @NotBlank
    private String visitorName;
    @NotBlank
    private String visitorPhone;
    @NotNull
    private VisitorType visitorType;
    private String vehicleNumber;
    private Long gateId;
    private String entryPhotoUrl;
    private String purpose;
}
