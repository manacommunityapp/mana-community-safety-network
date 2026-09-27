package com.manacommunity.safety.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "patrol_routes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatrolRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long communityId;

    @Column(nullable = false, length = 100)
    private String routeName;

    @Column(length = 500)
    private String description;

    private Integer estimatedDurationMinutes;

    private Integer checkpointCount;

    private Boolean isActive;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
