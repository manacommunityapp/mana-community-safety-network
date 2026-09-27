package com.manacommunity.safety.domain.entities;

import com.manacommunity.safety.domain.enums.DeviceType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "safety_hardware_devices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HardwareDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long communityId;

    @Column(nullable = false, length = 100)
    private String deviceName;

    @Column(nullable = false, unique = true, length = 50)
    private String deviceCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private DeviceType deviceType;

    @Column(length = 50)
    private String ipAddress;

    @Column(length = 50)
    private String macAddress;

    private Long gateId;

    @Column(length = 100)
    private String location;

    private Boolean isOnline;

    @Column(length = 50)
    private String firmwareVersion;

    private LocalDateTime lastHeartbeatAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
