package com.manacommunity.safety.domain.entities;

import com.manacommunity.safety.domain.enums.VisitorStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "visitor_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long communityId;

    private Long passId;

    private Long gateId;

    @Column(nullable = false, length = 100)
    private String visitorName;

    @Column(length = 20)
    private String visitorPhone;

    @Column(nullable = false, length = 50)
    private String flatNumber;

    @Column(length = 50)
    private String tower;

    @Column(length = 30)
    private String vehicleNumber;

    private LocalDateTime entryTime;

    private LocalDateTime exitTime;

    private Long checkedInGuardId;

    private Long checkedOutGuardId;

    @Column(length = 500)
    private String entryPhotoUrl;

    @Column(length = 500)
    private String exitPhotoUrl;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private VisitorStatus status;

    @Column(length = 500)
    private String remarks;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
