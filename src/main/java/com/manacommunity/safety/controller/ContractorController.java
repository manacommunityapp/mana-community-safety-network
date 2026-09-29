package com.manacommunity.safety.controller;

import com.manacommunity.safety.domain.entities.ContractorPass;
import com.manacommunity.safety.service.ContractorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/security/contractors")
@RequiredArgsConstructor
public class ContractorController {

    private final ContractorService contractorService;

    @GetMapping
    public ResponseEntity<List<ContractorPass>> getContractorPasses(
            @RequestHeader(value = "X-Community-Id", defaultValue = "1") Long communityId) {
        return ResponseEntity.ok(contractorService.getContractorPasses(communityId));
    }

    @PostMapping("/create")
    public ResponseEntity<ContractorPass> createContractorPass(
            @RequestHeader(value = "X-Community-Id", defaultValue = "1") Long communityId,
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
            @RequestBody Map<String, Object> body) {
        int workers = body.get("workerCount") != null ? Integer.parseInt(String.valueOf(body.get("workerCount"))) : 1;
        ContractorPass pass = contractorService.createContractorPass(
                communityId,
                (String) body.get("contractorName"),
                (String) body.get("vendorCompanyName"),
                (String) body.get("flatNumber"),
                (String) body.get("tower"),
                (String) body.get("workCategory"),
                workers,
                (String) body.get("workerDetails"),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(14),
                userId
        );
        return ResponseEntity.ok(pass);
    }

    @PostMapping("/{id}/entry")
    public ResponseEntity<ContractorPass> recordEntry(
            @PathVariable Long id,
            @RequestParam(value = "gateId", defaultValue = "1") Long gateId,
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long guardId) {
        return ResponseEntity.ok(contractorService.recordContractorGateEntry(id, gateId, guardId));
    }
}
