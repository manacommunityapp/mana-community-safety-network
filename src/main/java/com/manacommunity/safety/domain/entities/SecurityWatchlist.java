package com.manacommunity.safety.domain.entities;

import com.manacommunity.safety.domain.enums.WatchlistStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "security_watchlist", indexes = {
    @Index(name = "idx_watchlist_comm_status", columnList = "communityId, status"),
    @Index(name = "idx_watchlist_phone", columnList = "phone"),
    @Index(name = "idx_watchlist_plate", columnList = "licensePlate")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityWatchlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long communityId;

    @Column(nullable = false, length = 120)
    private String subjectName;

    @Column(length = 20)
    private String phone;

    @Column(length = 30)
    private String licensePlate;

    @Column(length = 50)
    private String idProofNumber;

    @Column(nullable = false, length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private WatchlistStatus status;

    private Long requestedByUserId;

    @Column(length = 100)
    private String requestedByName;

    private Long approvedByUserId;

    @Column(length = 100)
    private String approvedByName;

    private LocalDateTime effectiveFrom;

    private LocalDateTime expiresAt;

    @Column(length = 500)
    private String auditNotes;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
