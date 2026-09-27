package com.manacommunity.safety.domain.entities;

import com.manacommunity.safety.domain.enums.IncidentCategory;
import com.manacommunity.safety.domain.enums.IncidentSeverity;
import com.manacommunity.safety.domain.enums.IncidentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "safety_incidents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SafetyIncident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long communityId;

    @Column(nullable = false, unique = true, length = 50)
    private String incidentNumber;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private IncidentCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private IncidentSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private IncidentStatus status;

    @Column(length = 100)
    private String location;

    @Column(length = 50)
    private String tower;

    @Column(length = 50)
    private String flatNumber;

    private Long reportedByUserId;

    @Column(length = 100)
    private String reportedByName;

    private Long assignedInvestigatorUserId;

    @Column(length = 100)
    private String assignedInvestigatorName;

    @Column(length = 1000)
    private String evidencePhotos;

    @Column(length = 1000)
    private String resolutionNotes;

    private LocalDateTime resolvedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
