package com.manacommunity.safety.repository;

import com.manacommunity.safety.domain.entities.CabLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CabLogRepository extends JpaRepository<CabLog, Long> {
    List<CabLog> findByCommunityIdAndStatusOrderByCreatedAtDesc(Long communityId, String status);
    List<CabLog> findByCommunityIdOrderByCreatedAtDesc(Long communityId);
}
