package com.manacommunity.safety.dto.request;

import com.manacommunity.safety.domain.enums.DeviceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterDeviceRequest {
    @NotNull
    private Long communityId;
    @NotBlank
    private String deviceName;
    @NotBlank
    private String deviceCode;
    @NotNull
    private DeviceType deviceType;
    private String ipAddress;
    private String macAddress;
    private Long gateId;
    private String location;
    private String firmwareVersion;
}
