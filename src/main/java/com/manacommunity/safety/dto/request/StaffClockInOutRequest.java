package com.manacommunity.safety.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StaffClockInOutRequest {
    @NotNull
    private Long staffId;
    private Long gateId;
    private String verificationMethod; // RFID, QR, BIOMETRIC, MANUAL
}
