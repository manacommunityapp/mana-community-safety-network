package com.manacommunity.safety.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "security_audit_logs", indexes = {
    @Index(name = "idx_sec_audit_comm_time", columnList = "communityId, createdAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long communityId;

    @Column(nullable = false, length = 80)
    private String action; // VISITOR_CREATED, ACCESS_GRANTED, WATCHLIST_APPROVED, etc.

    private Long performedByUserId;

    @Column(length = 50)
    private String performedByRole;

    @Column(length = 50)
    private String targetEntityType;

    private Long targetEntityId;

    @Column(length = 1000)
    private String details;

    @Column(length = 50)
    private String ipAddress;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
