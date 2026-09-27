package com.manacommunity.safety.dto.request;

import com.manacommunity.safety.domain.enums.IncidentCategory;
import com.manacommunity.safety.domain.enums.IncidentSeverity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSafetyIncidentRequest {
    @NotNull
    private Long communityId;
    @NotBlank
    private String title;
    private String description;
    @NotNull
    private IncidentCategory category;
    @NotNull
    private IncidentSeverity severity;
    private String location;
    private String tower;
    private String flatNumber;
    private String evidencePhotos;
}
