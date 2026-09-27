package com.manacommunity.safety.repository;

import com.manacommunity.safety.domain.entities.StaffAttendanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StaffAttendanceLogRepository extends JpaRepository<StaffAttendanceLog, Long> {
    Optional<StaffAttendanceLog> findFirstByStaffIdAndCheckOutTimeIsNullOrderByCheckInTimeDesc(Long staffId);
    List<StaffAttendanceLog> findByCommunityIdAndCheckInTimeAfter(Long communityId, LocalDateTime after);
    Long countByCommunityIdAndCheckOutTimeIsNull(Long communityId);
}
