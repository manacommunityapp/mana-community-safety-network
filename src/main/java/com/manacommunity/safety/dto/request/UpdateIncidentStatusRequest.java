package com.manacommunity.safety.dto.request;

import com.manacommunity.safety.domain.enums.IncidentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateIncidentStatusRequest {
    @NotNull
    private IncidentStatus status;
    private String note;
    private String resolutionNotes;
}
