package com.manacommunity.safety.domain.entities;

import com.manacommunity.safety.domain.enums.VehicleAccessType;
import com.manacommunity.safety.domain.enums.VehicleType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "community_vehicles", uniqueConstraints = {
    @UniqueConstraint(name = "uq_community_license_plate", columnNames = {"communityId", "licensePlate"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long communityId;

    private Long residentId;

    @Column(nullable = false, length = 30)
    private String licensePlate;

    @Column(length = 50)
    private String rfidTagNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VehicleType vehicleType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VehicleAccessType accessType;

    @Column(length = 50)
    private String makeModel;

    @Column(length = 30)
    private String color;

    @Column(length = 50)
    private String flatNumber;

    @Column(length = 50)
    private String tower;

    @Column(length = 50)
    private String parkingSlotNumber;

    private Boolean isEv;

    private Boolean isActive;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
