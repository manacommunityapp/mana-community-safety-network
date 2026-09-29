package com.manacommunity.safety.domain.entities;

import com.manacommunity.safety.domain.enums.VisitorType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "security_access_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityAccessRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long communityId;

    @Column(nullable = false, length = 100)
    private String ruleName;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private VisitorType visitorType;

    private Integer startHour; // e.g. 20 (8 PM)

    private Integer endHour; // e.g. 6 (6 AM)

    @Builder.Default
    private Boolean requireAdminApproval = false;

    @Builder.Default
    private Boolean requireManualVerification = false;

    @Builder.Default
    private Boolean blockAccess = false;

    @Builder.Default
    private Boolean isActive = true;

    @Column(length = 500)
    private String description;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
