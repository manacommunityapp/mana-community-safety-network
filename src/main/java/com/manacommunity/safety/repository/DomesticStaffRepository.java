package com.manacommunity.safety.repository;

import com.manacommunity.safety.domain.entities.DomesticStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DomesticStaffRepository extends JpaRepository<DomesticStaff, Long> {
    List<DomesticStaff> findByCommunityIdAndIsActiveTrue(Long communityId);
    Optional<DomesticStaff> findByCommunityIdAndRfidTag(Long communityId, String rfidTag);
    Optional<DomesticStaff> findByCommunityIdAndPasscode(Long communityId, String passcode);
}
