package com.manacommunity.safety.domain.entities;

import com.manacommunity.safety.domain.enums.StaffType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "staff_attendance_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffAttendanceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long communityId;

    @Column(nullable = false)
    private Long staffId;

    @Column(nullable = false, length = 100)
    private String staffName;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private StaffType staffType;

    private Long gateId;

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    private Long durationMinutes;

    @Column(length = 30)
    private String verificationMethod; // RFID, QR, BIOMETRIC, MANUAL

    @CreationTimestamp
    private LocalDateTime createdAt;
}
