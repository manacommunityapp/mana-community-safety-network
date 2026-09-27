package com.manacommunity.safety.domain.entities;

import com.manacommunity.safety.domain.enums.ParkingViolationType;
import com.manacommunity.safety.domain.enums.ViolationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "parking_violations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingViolation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long communityId;

    @Column(nullable = false, length = 30)
    private String vehicleNumber;

    @Column(length = 50)
    private String parkingSlotNumber;

    @Column(length = 50)
    private String tower;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ParkingViolationType violationType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ViolationStatus status;

    @Column(length = 500)
    private String description;

    @Column(length = 500)
    private String photoEvidenceUrl;

    private BigDecimal fineAmount;

    private Long reportedByUserId;

    @Column(length = 100)
    private String reportedByName;

    private Long resolvedByUserId;

    private LocalDateTime resolvedAt;

    @Column(length = 500)
    private String resolutionNotes;

    @CreationTimestamp
    private LocalDateTime reportedAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
