package com.manacommunity.safety.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "gate_booths")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GateBooth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long communityId;

    @Column(nullable = false, length = 100)
    private String gateName;

    @Column(length = 20)
    private String gateCode;

    @Column(length = 20)
    private String direction; // ENTRY, EXIT, BIDIRECTIONAL

    private Boolean boomBarrierConnected;

    @Column(length = 100)
    private String cameraIp;

    private Boolean isActive;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
