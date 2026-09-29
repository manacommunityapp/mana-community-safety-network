package com.manacommunity.safety.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "security_contractor_passes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractorPass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long communityId;

    @Column(nullable = false, length = 120)
    private String contractorName;

    @Column(length = 120)
    private String vendorCompanyName;

    @Column(nullable = false, length = 50)
    private String flatNumber;

    @Column(length = 50)
    private String tower;

    @Column(length = 80)
    private String workCategory; // CARPENTRY, PLUMBING, RENOVATION, ELECTRICAL, etc.

    @Builder.Default
    private Integer workerCount = 1;

    @Column(length = 1000)
    private String workerDetails;

    private LocalDateTime validFrom;

    private LocalDateTime validUntil;

    @Builder.Default
    private Boolean approvedByAdmin = false;

    private Long adminUserId;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "APPROVED"; // PENDING, APPROVED, ACTIVE, COMPLETED, REVOKED

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
