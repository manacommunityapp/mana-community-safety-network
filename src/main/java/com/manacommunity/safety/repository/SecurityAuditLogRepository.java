package com.manacommunity.safety.repository;

import com.manacommunity.safety.domain.entities.SecurityAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecurityAuditLogRepository extends JpaRepository<SecurityAuditLog, Long> {
    Page<SecurityAuditLog> findByCommunityIdOrderByCreatedAtDesc(Long communityId, Pageable pageable);
    List<SecurityAuditLog> findTop100ByCommunityIdOrderByCreatedAtDesc(Long communityId);
}
