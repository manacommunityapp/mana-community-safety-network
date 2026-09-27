package com.manacommunity.safety.dto.request;

import com.manacommunity.safety.domain.enums.DeliveryDropLocation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LogDeliveryRequest {
    @NotNull
    private Long communityId;
    @NotBlank
    private String flatNumber;
    private String tower;
    @NotBlank
    private String courierCompany;
    private String deliveryAgentName;
    private String deliveryAgentPhone;
    private Integer packageCount;
    @NotNull
    private DeliveryDropLocation dropLocation;
    private String lockerSlot;
    private String packagePhotoUrl;
}
