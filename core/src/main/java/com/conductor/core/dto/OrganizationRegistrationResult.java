package com.conductor.core.dto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public  class OrganizationRegistrationResult {
    private boolean success;
    private String message;
    private String registrationId;
    private List<String> errors;
}