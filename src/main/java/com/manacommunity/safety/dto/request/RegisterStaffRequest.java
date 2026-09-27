package com.manacommunity.safety.dto.request;

import com.manacommunity.safety.domain.enums.StaffType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterStaffRequest {
    @NotNull
    private Long communityId;
    @NotBlank
    private String staffName;
    @NotBlank
    private String phone;
    @NotNull
    private StaffType staffType;
    private String govtIdType;
    private String govtIdNumber;
    private String photoUrl;
    private String rfidTag;
    private String assignedFlats;
}
