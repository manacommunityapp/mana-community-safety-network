package com.manacommunity.safety.repository;

import com.manacommunity.safety.domain.entities.VehicleAccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VehicleAccessLogRepository extends JpaRepository<VehicleAccessLog, Long> {
    List<VehicleAccessLog> findByCommunityIdOrderByAccessTimeDesc(Long communityId);
    Long countByCommunityIdAndDirectionAndAccessTimeAfter(Long communityId, String direction, LocalDateTime after);
}
