package com.manacommunity.safety.repository;

import com.manacommunity.safety.domain.entities.SafetyIncident;
import com.manacommunity.safety.domain.enums.IncidentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SafetyIncidentRepository extends JpaRepository<SafetyIncident, Long> {
    List<SafetyIncident> findByCommunityIdOrderByCreatedAtDesc(Long communityId);
    List<SafetyIncident> findByCommunityIdAndStatusNotInOrderByCreatedAtDesc(Long communityId, List<IncidentStatus> closedStatuses);
    Optional<SafetyIncident> findByIncidentNumber(String incidentNumber);
    Long countByCommunityIdAndStatusNotIn(Long communityId, List<IncidentStatus> closedStatuses);
}
