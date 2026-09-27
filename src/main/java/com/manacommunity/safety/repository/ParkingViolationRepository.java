package com.manacommunity.safety.repository;

import com.manacommunity.safety.domain.entities.ParkingViolation;
import com.manacommunity.safety.domain.enums.ViolationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingViolationRepository extends JpaRepository<ParkingViolation, Long> {
    List<ParkingViolation> findByCommunityIdOrderByReportedAtDesc(Long communityId);
    List<ParkingViolation> findByCommunityIdAndStatus(Long communityId, ViolationStatus status);
    Long countByCommunityIdAndStatusNotIn(Long communityId, List<ViolationStatus> closedStatuses);
}
