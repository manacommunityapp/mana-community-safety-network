package com.manacommunity.safety.repository;

import com.manacommunity.safety.domain.entities.VisitorPass;
import com.manacommunity.safety.domain.enums.VisitorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VisitorPassRepository extends JpaRepository<VisitorPass, Long> {
    List<VisitorPass> findByCommunityIdAndResidentIdOrderByCreatedAtDesc(Long communityId, Long residentId);
    List<VisitorPass> findByCommunityIdAndStatusOrderByCreatedAtDesc(Long communityId, VisitorStatus status);
    Optional<VisitorPass> findByCommunityIdAndPassCode(Long communityId, String passCode);
    List<VisitorPass> findByCommunityIdAndFlatNumberOrderByCreatedAtDesc(Long communityId, String flatNumber);
}
