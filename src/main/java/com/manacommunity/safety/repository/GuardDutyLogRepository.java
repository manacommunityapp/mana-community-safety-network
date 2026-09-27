package com.manacommunity.safety.repository;

import com.manacommunity.safety.domain.entities.GuardDutyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuardDutyLogRepository extends JpaRepository<GuardDutyLog, Long> {
    List<GuardDutyLog> findByCommunityIdAndStatus(Long communityId, String status);
}
