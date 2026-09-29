package com.manacommunity.safety.service;

import com.manacommunity.safety.domain.entities.SecurityAccessRule;
import com.manacommunity.safety.domain.enums.SecurityDecision;
import com.manacommunity.safety.domain.enums.VisitorType;
import com.manacommunity.safety.repository.SecurityAccessRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityRulesEngine {

    private final SecurityAccessRuleRepository ruleRepository;

    @Transactional(readOnly = true)
    public SecurityDecision evaluateAccess(Long communityId, VisitorType visitorType, LocalTime currentTime) {
        if (visitorType == null) {
            return SecurityDecision.ALLOWED;
        }

        List<SecurityAccessRule> rules = ruleRepository.findByCommunityIdAndVisitorTypeAndIsActiveTrue(communityId, visitorType);
        int currentHour = currentTime.getHour();

        for (SecurityAccessRule rule : rules) {
            if (rule.getStartHour() != null && rule.getEndHour() != null) {
                boolean inWindow;
                if (rule.getStartHour() <= rule.getEndHour()) {
                    inWindow = currentHour >= rule.getStartHour() && currentHour < rule.getEndHour();
                } else {
                    // Spans overnight (e.g. 20 to 6)
                    inWindow = currentHour >= rule.getStartHour() || currentHour < rule.getEndHour();
                }

                if (inWindow) {
                    if (Boolean.TRUE.equals(rule.getBlockAccess())) {
                        return SecurityDecision.DENIED;
                    }
                    if (Boolean.TRUE.equals(rule.getRequireAdminApproval())) {
                        return SecurityDecision.ESCALATED;
                    }
                    if (Boolean.TRUE.equals(rule.getRequireManualVerification())) {
                        return SecurityDecision.MANUAL_VERIFICATION_REQUIRED;
                    }
                }
            }
        }
        return SecurityDecision.ALLOWED;
    }
}
