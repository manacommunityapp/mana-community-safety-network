package com.manacommunity.safety.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "guard_duty_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuardDutyLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long communityId;

    @Column(nullable = false)
    private Long guardUserId;

    @Column(nullable = false, length = 100)
    private String guardName;

    private Long gateId;

    @Column(length = 50)
    private String shiftName; // MORNING, EVENING, NIGHT

    private LocalDateTime shiftStartTime;

    private LocalDateTime shiftEndTime;

    @Column(length = 30)
    private String status; // ON_DUTY, COMPLETED, ABSENT

    @CreationTimestamp
    private LocalDateTime createdAt;
}
