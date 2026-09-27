package com.manacommunity.safety.repository;

import com.manacommunity.safety.domain.entities.PatrolLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatrolLogRepository extends JpaRepository<PatrolLog, Long> {
    List<PatrolLog> findByCommunityIdOrderByStartTimeDesc(Long communityId);
}
