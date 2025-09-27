package com.conductor.core.controller.api.organization;

import com.conductor.core.dto.OrganizationModificationRequest;
import com.conductor.core.model.ResourceType;
import com.conductor.core.model.org.Organization;
import com.conductor.core.security.fiber.FiberPermissionEvaluator;
import com.conductor.core.security.fiber.FiberPermissionEvaluatorChain;
import com.conductor.core.service.OrganizationModificationService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/organizations/")
@Validated
@RequiredArgsConstructor
public class OrganizationModificationController {

    private final FiberPermissionEvaluator permissionEvaluator;
    private final FiberPermissionEvaluatorChain evaluatorChain;

    private final OrganizationModificationService modificationService;

    @PostMapping("/{organization-id}/modify")
    public ResponseEntity<?> modify(
            @PathVariable("organization-id")
            @NotBlank(message = "Organization Id is required")
            @Size(max = 100)
            String organizationExternalId,
            OrganizationModificationRequest request,
            Authentication auth){

        evaluatorChain
                .isOperator()
                .addPermission(organizationExternalId,
                                ResourceType.ORGANIZATION,
                                Organization.getOwnerPermission())
                .evaluate(auth);

        modificationService.modify(organizationExternalId,request);

        return ResponseEntity.ok().build();
    }

}
