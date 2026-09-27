package com.manacommunity.safety.dto.request;

import com.manacommunity.safety.domain.enums.PassType;
import com.manacommunity.safety.domain.enums.VisitorType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateVisitorPassRequest {
    @NotNull
    private Long communityId;
    @NotBlank
    private String flatNumber;
    private String tower;
    @NotBlank
    private String visitorName;
    private String visitorPhone;
    @NotNull
    private VisitorType visitorType;
    @NotNull
    private PassType passType;
    private String vehicleNumber;
    private Integer expectedGuestCount;
    private LocalDateTime expectedArrival;
    private LocalDateTime validUntil;
    private String purpose;
}
