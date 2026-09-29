package com.manacommunity.safety.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "security_cab_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CabLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long communityId;

    @Column(nullable = false, length = 100)
    private String driverName;

    @Column(length = 50)
    private String cabCompany; // UBER, OLA, RAPIDO, PRIVATE

    @Column(nullable = false, length = 30)
    private String vehicleNumber;

    @Column(nullable = false, length = 50)
    private String flatNumber;

    @Column(length = 50)
    private String tower;

    private Long passengerResidentId;

    @Column(length = 100)
    private String passengerName;

    @Column(length = 30)
    private String pickupType; // PICKUP, DROP, SCHEDULED

    private Long gateId;

    private Long guardId;

    private LocalDateTime entryTime;

    private LocalDateTime exitTime;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "ENTERED"; // ENTERED, EXITED, CANCELLED

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
