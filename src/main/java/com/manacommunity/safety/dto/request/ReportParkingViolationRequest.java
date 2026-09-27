package com.manacommunity.safety.dto.request;

import com.manacommunity.safety.domain.enums.ParkingViolationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReportParkingViolationRequest {
    @NotNull
    private Long communityId;
    @NotBlank
    private String vehicleNumber;
    private String parkingSlotNumber;
    private String tower;
    @NotNull
    private ParkingViolationType violationType;
    private String description;
    private String photoEvidenceUrl;
    private BigDecimal fineAmount;
}
