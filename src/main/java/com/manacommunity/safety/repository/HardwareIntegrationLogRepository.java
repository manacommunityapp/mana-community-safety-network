package com.manacommunity.safety.repository;

import com.manacommunity.safety.domain.entities.HardwareIntegrationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HardwareIntegrationLogRepository extends JpaRepository<HardwareIntegrationLog, Long> {
    List<HardwareIntegrationLog> findByCommunityIdOrderByTimestampDesc(Long communityId);
}
