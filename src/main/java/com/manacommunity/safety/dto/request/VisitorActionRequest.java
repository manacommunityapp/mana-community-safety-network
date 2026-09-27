package com.manacommunity.safety.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VisitorActionRequest {
    @NotNull
    private Boolean approved;
    private String remarks;
}
