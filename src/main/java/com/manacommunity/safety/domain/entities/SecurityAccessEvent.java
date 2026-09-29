package com.manacommunity.safety.domain.entities;

import com.manacommunity.safety.domain.enums.SecurityDecision;
import com.manacommunity.safety.domain.enums.SecurityEventType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "security_access_events", indexes = {
    @Index(name = "idx_sec_event_comm_time", columnList = "communityId, timestamp"),
    @Index(name = "idx_sec_event_subject", columnList = "subjectType, subjectId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityAccessEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long communityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private SecurityEventType eventType;

    @Column(length = 50)
    private String subjectType; // VISITOR, VEHICLE, DELIVERY, STAFF, CONTRACTOR, CAB

    private Long subjectId;

    @Column(length = 150)
    private String subjectIdentifier; // Name, Plate, Phone

    private Long gateId;

    @Column(length = 100)
    private String gateName;

    private Long deviceId;

    private Long residentId;

    @Column(length = 50)
    private String flatNumber;

    @Column(length = 50)
    private String tower;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private SecurityDecision decision;

    @Column(length = 255)
    private String decisionReason;

    private Long guardId;

    @Column(length = 1000)
    private String metadataJson;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime timestamp;
}
