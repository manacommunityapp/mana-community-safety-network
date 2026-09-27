package com.manacommunity.safety.domain.entities;

import com.manacommunity.safety.domain.enums.StaffType;
import com.manacommunity.safety.domain.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "domestic_staff")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DomesticStaff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long communityId;

    @Column(nullable = false, length = 100)
    private String staffName;

    @Column(nullable = false, length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StaffType staffType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VerificationStatus verificationStatus;

    @Column(length = 50)
    private String govtIdType; // AADHAAR, PAN, VOTER_ID, PASSPORT

    @Column(length = 50)
    private String govtIdNumber;

    @Column(length = 500)
    private String photoUrl;

    @Column(length = 50)
    private String rfidTag;

    @Column(length = 20)
    private String passcode;

    @Column(length = 500)
    private String assignedFlats; // Comma separated, e.g. "T1-402, T2-101"

    private Double rating;

    private Boolean isActive;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
