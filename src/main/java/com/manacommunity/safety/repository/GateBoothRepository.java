package com.manacommunity.safety.repository;

import com.manacommunity.safety.domain.entities.GateBooth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GateBoothRepository extends JpaRepository<GateBooth, Long> {
    List<GateBooth> findByCommunityIdAndIsActiveTrue(Long communityId);
    Optional<GateBooth> findByCommunityIdAndGateCode(Long communityId, String gateCode);
}
