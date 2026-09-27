package com.manacommunity.safety.domain.entities;

import com.manacommunity.safety.domain.enums.DeviceType;
import com.manacommunity.safety.domain.enums.HardwareEventStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "hardware_integration_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HardwareIntegrationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long communityId;

    private Long deviceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private DeviceType deviceType;

    @Column(nullable = false, length = 50)
    private String eventType; // PLATE_DETECTED, TAG_SCANNED, BARRIER_TRIGGERED, MOTION_ALERT

    @Column(length = 2000)
    private String rawPayload;

    @Column(length = 50)
    private String parsedKey; // e.g. license plate or tag number

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private HardwareEventStatus status;

    private Long executionTimeMs;

    @Column(length = 500)
    private String responseMessage;

    @CreationTimestamp
    private LocalDateTime timestamp;
}
