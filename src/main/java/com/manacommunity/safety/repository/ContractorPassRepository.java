package com.manacommunity.safety.repository;

import com.manacommunity.safety.domain.entities.ContractorPass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractorPassRepository extends JpaRepository<ContractorPass, Long> {
    List<ContractorPass> findByCommunityIdOrderByCreatedAtDesc(Long communityId);
    List<ContractorPass> findByCommunityIdAndStatus(Long communityId, String status);
}
