package com.manacommunity.safety.controller;

import com.manacommunity.safety.domain.entities.SecurityAccessRule;
import com.manacommunity.safety.domain.enums.VisitorType;
import com.manacommunity.safety.repository.SecurityAccessRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/security/rules")
@RequiredArgsConstructor
public class SecurityRulesController {

    private final SecurityAccessRuleRepository ruleRepository;

    @GetMapping
    public ResponseEntity<List<SecurityAccessRule>> getRules(
            @RequestHeader(value = "X-Community-Id", defaultValue = "1") Long communityId) {
        return ResponseEntity.ok(ruleRepository.findByCommunityIdAndIsActiveTrue(communityId));
    }

    @PostMapping
    public ResponseEntity<SecurityAccessRule> createRule(
            @RequestHeader(value = "X-Community-Id", defaultValue = "1") Long communityId,
            @RequestBody Map<String, Object> body) {
        SecurityAccessRule rule = SecurityAccessRule.builder()
                .communityId(communityId)
                .ruleName((String) body.get("ruleName"))
                .visitorType(body.get("visitorType") != null ? VisitorType.valueOf((String) body.get("visitorType")) : null)
                .startHour(body.get("startHour") != null ? Integer.parseInt(String.valueOf(body.get("startHour"))) : null)
                .endHour(body.get("endHour") != null ? Integer.parseInt(String.valueOf(body.get("endHour"))) : null)
                .requireAdminApproval(body.get("requireAdminApproval") != null && Boolean.parseBoolean(String.valueOf(body.get("requireAdminApproval"))))
                .requireManualVerification(body.get("requireManualVerification") != null && Boolean.parseBoolean(String.valueOf(body.get("requireManualVerification"))))
                .blockAccess(body.get("blockAccess") != null && Boolean.parseBoolean(String.valueOf(body.get("blockAccess"))))
                .isActive(true)
                .description((String) body.get("description"))
                .build();

        return ResponseEntity.ok(ruleRepository.save(rule));
    }
}
