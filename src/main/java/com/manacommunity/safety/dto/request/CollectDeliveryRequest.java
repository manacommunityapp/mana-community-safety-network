package com.manacommunity.safety.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CollectDeliveryRequest {
    @NotBlank
    private String collectionOtp;
}
