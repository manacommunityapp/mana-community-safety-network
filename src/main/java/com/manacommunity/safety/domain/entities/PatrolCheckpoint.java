package com.manacommunity.safety.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "patrol_checkpoints")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatrolCheckpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long routeId;

    @Column(nullable = false, length = 100)
    private String checkpointName;

    @Column(length = 100)
    private String location; // e.g. "Basement 2 East Wing"

    @Column(length = 50)
    private String nfcTagId;

    @Column(length = 100)
    private String qrCode;

    private Integer sequenceOrder;
}
