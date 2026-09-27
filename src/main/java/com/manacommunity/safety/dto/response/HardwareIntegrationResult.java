package com.manacommunity.safety.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HardwareIntegrationResult {
    private Boolean authorized;
    private Boolean barrierActuated;
    private String matchedEntityType; // RESIDENT_VEHICLE, VISITOR_VEHICLE, STAFF, UNKNOWN
    private String message;
    private String entityName;
    private String flatNumber;
}
