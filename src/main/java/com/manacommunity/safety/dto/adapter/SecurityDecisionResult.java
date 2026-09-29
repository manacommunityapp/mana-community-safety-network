package com.manacommunity.safety.dto.adapter;

import com.manacommunity.safety.domain.enums.SecurityDecision;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityDecisionResult {
    private SecurityDecision decision;
    private boolean barrierActuated;
    private String matchedEntityType; // VISITOR, VEHICLE, STAFF, CONTRACTOR, CAB, UNKNOWN
    private String entityName;
    private String flatNumber;
    private String message;
    private boolean alertTriggered;
    private String alertDetails;
}
