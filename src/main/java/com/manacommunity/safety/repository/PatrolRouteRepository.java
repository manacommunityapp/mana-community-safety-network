package com.manacommunity.safety.repository;

import com.manacommunity.safety.domain.entities.PatrolRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatrolRouteRepository extends JpaRepository<PatrolRoute, Long> {
    List<PatrolRoute> findByCommunityIdAndIsActiveTrue(Long communityId);
}
