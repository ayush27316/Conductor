package com.conductor.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApproveOrganizationRequest {

    @NotNull
    @JsonProperty("registration_id")
    String registrationId;

    String note;
}
