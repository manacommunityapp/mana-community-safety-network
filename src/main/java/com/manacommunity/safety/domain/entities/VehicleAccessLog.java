package com.manacommunity.safety.domain.entities;

import com.manacommunity.safety.domain.enums.VehicleAccessType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_access_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleAccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long communityId;

    private Long vehicleId;

    @Column(nullable = false, length = 30)
    private String licensePlate;

    @Column(length = 50)
    private String rfidTag;

    private Long gateId;

    @Column(length = 10)
    private String direction; // IN, OUT

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private VehicleAccessType accessType;

    @Column(length = 30)
    private String triggerSource; // ANPR, RFID, QR, MANUAL

    private Boolean barrierActuated;

    private Boolean isAuthorized;

    @Column(length = 500)
    private String snapshotPhotoUrl;

    @Column(length = 200)
    private String gateName;

    @CreationTimestamp
    private LocalDateTime accessTime;
}
