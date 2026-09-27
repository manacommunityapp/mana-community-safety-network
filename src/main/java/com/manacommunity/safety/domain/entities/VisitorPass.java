package com.manacommunity.safety.domain.entities;

import com.manacommunity.safety.domain.enums.PassType;
import com.manacommunity.safety.domain.enums.VisitorStatus;
import com.manacommunity.safety.domain.enums.VisitorType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "visitor_passes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitorPass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long communityId;

    @Column(nullable = false)
    private Long residentId;

    @Column(nullable = false, length = 100)
    private String residentName;

    @Column(nullable = false, length = 50)
    private String flatNumber;

    @Column(length = 50)
    private String tower;

    @Column(nullable = false, length = 100)
    private String visitorName;

    @Column(length = 20)
    private String visitorPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VisitorType visitorType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PassType passType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VisitorStatus status;

    @Column(length = 10)
    private String passCode; // 6-digit verification code

    @Column(length = 500)
    private String qrPayload;

    @Column(length = 30)
    private String vehicleNumber;

    private Integer expectedGuestCount;

    private LocalDateTime expectedArrival;

    private LocalDateTime validUntil;

    private LocalDateTime approvedAt;

    private Long approvedByUserId;

    @Column(length = 500)
    private String purpose;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
