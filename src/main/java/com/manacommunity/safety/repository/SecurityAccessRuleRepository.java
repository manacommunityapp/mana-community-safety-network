package com.manacommunity.safety.repository;

import com.manacommunity.safety.domain.entities.SecurityAccessRule;
import com.manacommunity.safety.domain.enums.VisitorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecurityAccessRuleRepository extends JpaRepository<SecurityAccessRule, Long> {
    List<SecurityAccessRule> findByCommunityIdAndIsActiveTrue(Long communityId);
    List<SecurityAccessRule> findByCommunityIdAndVisitorTypeAndIsActiveTrue(Long communityId, VisitorType visitorType);
}
