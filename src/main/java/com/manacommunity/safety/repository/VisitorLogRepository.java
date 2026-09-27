package com.manacommunity.safety.repository;

import com.manacommunity.safety.domain.entities.VisitorLog;
import com.manacommunity.safety.domain.enums.VisitorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitorLogRepository extends JpaRepository<VisitorLog, Long> {
    List<VisitorLog> findByCommunityIdAndStatus(Long communityId, VisitorStatus status);
    List<VisitorLog> findByCommunityIdOrderByEntryTimeDesc(Long communityId);
    Long countByCommunityIdAndStatus(Long communityId, VisitorStatus status);
    List<VisitorLog> findByCommunityIdAndEntryTimeBetween(Long communityId, LocalDateTime start, LocalDateTime end);
}
