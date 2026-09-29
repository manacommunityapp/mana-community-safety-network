package com.manacommunity.safety.repository;

import com.manacommunity.safety.domain.entities.SecurityWatchlist;
import com.manacommunity.safety.domain.enums.WatchlistStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SecurityWatchlistRepository extends JpaRepository<SecurityWatchlist, Long> {
    List<SecurityWatchlist> findByCommunityIdAndStatus(Long communityId, WatchlistStatus status);
    List<SecurityWatchlist> findByCommunityIdOrderByCreatedAtDesc(Long communityId);
    Optional<SecurityWatchlist> findByCommunityIdAndPhoneAndStatus(Long communityId, String phone, WatchlistStatus status);
    Optional<SecurityWatchlist> findByCommunityIdAndLicensePlateIgnoreCaseAndStatus(Long communityId, String licensePlate, WatchlistStatus status);
}
