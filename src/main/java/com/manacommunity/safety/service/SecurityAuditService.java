package com.manacommunity.safety.service;

import com.manacommunity.safety.domain.entities.SecurityAuditLog;
import com.manacommunity.safety.repository.SecurityAuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityAuditService {

    private final SecurityAuditLogRepository auditLogRepository;

    @Transactional
    public void recordAction(Long communityId, String action, Long userId, String role, String targetType, Long targetId, String details, String ipAddress) {
        try {
            SecurityAuditLog logEntry = SecurityAuditLog.builder()
                    .communityId(communityId)
                    .action(action)
                    .performedByUserId(userId)
                    .performedByRole(role)
                    .targetEntityType(targetType)
                    .targetEntityId(targetId)
                    .details(details)
                    .ipAddress(ipAddress)
                    .build();
            auditLogRepository.save(logEntry);
        } catch (Exception e) {
            log.error("Failed to persist security audit log for communityId: {}, action: {}", communityId, action, e);
        }
    }
}
