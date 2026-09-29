package com.manacommunity.safety.service;

import com.manacommunity.common.exception.ResourceNotFoundException;
import com.manacommunity.safety.domain.entities.ContractorPass;
import com.manacommunity.safety.domain.enums.SecurityDecision;
import com.manacommunity.safety.domain.enums.SecurityEventType;
import com.manacommunity.safety.repository.ContractorPassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractorService {

    private final ContractorPassRepository contractorPassRepository;
    private final SecurityEventService securityEventService;
    private final SecurityAuditService auditService;

    @Transactional
    public ContractorPass createContractorPass(Long communityId, String contractorName, String vendorCompanyName,
                                               String flatNumber, String tower, String workCategory, int workerCount,
                                               String workerDetails, LocalDateTime validFrom, LocalDateTime validUntil,
                                               Long residentOrAdminId) {
        ContractorPass pass = ContractorPass.builder()
                .communityId(communityId)
                .contractorName(contractorName)
                .vendorCompanyName(vendorCompanyName)
                .flatNumber(flatNumber)
                .tower(tower)
                .workCategory(workCategory)
                .workerCount(workerCount)
                .workerDetails(workerDetails)
                .validFrom(validFrom != null ? validFrom : LocalDateTime.now())
                .validUntil(validUntil != null ? validUntil : LocalDateTime.now().plusDays(7))
                .approvedByAdmin(true)
                .adminUserId(residentOrAdminId)
                .status("APPROVED")
                .build();

        ContractorPass saved = contractorPassRepository.save(pass);
        auditService.recordAction(communityId, "CONTRACTOR_PASS_CREATED", residentOrAdminId, "ADMIN",
                "CONTRACTOR_PASS", saved.getId(), "Contractor pass for " + contractorName + " (" + workCategory + ")", null);
        return saved;
    }

    @Transactional
    public ContractorPass recordContractorGateEntry(Long passId, Long gateId, Long guardId) {
        ContractorPass pass = contractorPassRepository.findById(passId)
                .orElseThrow(() -> new ResourceNotFoundException("ContractorPass", "id", passId));

        if (pass.getValidUntil() != null && LocalDateTime.now().isAfter(pass.getValidUntil())) {
            securityEventService.recordEvent(pass.getCommunityId(), SecurityEventType.ACCESS_DENIED, "CONTRACTOR",
                    pass.getId(), pass.getContractorName(), gateId, "Gate " + gateId, null, null, pass.getFlatNumber(),
                    pass.getTower(), SecurityDecision.DENIED, "Contractor pass expired", guardId, null);
            throw new IllegalStateException("Contractor pass is expired.");
        }

        pass.setStatus("ACTIVE");
        ContractorPass saved = contractorPassRepository.save(pass);

        securityEventService.recordEvent(pass.getCommunityId(), SecurityEventType.CONTRACTOR_ENTRY, "CONTRACTOR",
                pass.getId(), pass.getContractorName(), gateId, "Gate " + gateId, null, null, pass.getFlatNumber(),
                pass.getTower(), SecurityDecision.ALLOWED, "Contractor entry verified (" + pass.getWorkerCount() + " workers)", guardId, null);

        auditService.recordAction(pass.getCommunityId(), "CONTRACTOR_ENTERED", guardId, "GUARD",
                "CONTRACTOR_PASS", saved.getId(), "Contractor checked in: " + pass.getContractorName(), null);

        return saved;
    }

    @Transactional(readOnly = true)
    public List<ContractorPass> getContractorPasses(Long communityId) {
        return contractorPassRepository.findByCommunityIdOrderByCreatedAtDesc(communityId);
    }
}
