package com.manacommunity.safety.repository;

import com.manacommunity.safety.domain.entities.SecurityAccessEvent;
import com.manacommunity.safety.domain.enums.SecurityEventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SecurityAccessEventRepository extends JpaRepository<SecurityAccessEvent, Long> {
    List<SecurityAccessEvent> findTop50ByCommunityIdOrderByTimestampDesc(Long communityId);
    Page<SecurityAccessEvent> findByCommunityIdOrderByTimestampDesc(Long communityId, Pageable pageable);
    List<SecurityAccessEvent> findByCommunityIdAndEventTypeOrderByTimestampDesc(Long communityId, SecurityEventType eventType);
    long countByCommunityIdAndTimestampBetween(Long communityId, LocalDateTime start, LocalDateTime end);
}
