package com.manacommunity.safety.dto.adapter;

import com.manacommunity.safety.domain.enums.DeviceType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityDeviceEvent {
    private Long communityId;
    private DeviceType deviceType;
    private String deviceCode;
    private Long gateId;
    private String rawData; // QR Token, License Plate, RFID Tag, Face ID, etc.
    private String direction; // IN, OUT
    private Double confidenceScore;
    private String snapshotPhotoUrl;
    private LocalDateTime timestamp;
}
