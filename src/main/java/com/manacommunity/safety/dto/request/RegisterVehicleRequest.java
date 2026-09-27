package com.manacommunity.safety.dto.request;

import com.manacommunity.safety.domain.enums.VehicleAccessType;
import com.manacommunity.safety.domain.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterVehicleRequest {
    @NotNull
    private Long communityId;
    private Long residentId;
    @NotBlank
    private String licensePlate;
    private String rfidTagNumber;
    @NotNull
    private VehicleType vehicleType;
    @NotNull
    private VehicleAccessType accessType;
    private String makeModel;
    private String color;
    private String flatNumber;
    private String tower;
    private String parkingSlotNumber;
    private Boolean isEv;
}
