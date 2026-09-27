package com.manacommunity.safety.repository;

import com.manacommunity.safety.domain.entities.DeliveryLog;
import com.manacommunity.safety.domain.enums.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryLogRepository extends JpaRepository<DeliveryLog, Long> {
    List<DeliveryLog> findByCommunityIdAndFlatNumberOrderByCreatedAtDesc(Long communityId, String flatNumber);
    List<DeliveryLog> findByCommunityIdAndStatus(Long communityId, DeliveryStatus status);
    Optional<DeliveryLog> findByCommunityIdAndCollectionOtp(Long communityId, String otp);
    Long countByCommunityIdAndStatusIn(Long communityId, List<DeliveryStatus> pendingStatuses);
}
