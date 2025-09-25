package com.conductor.core.controller.api.organization;

import com.conductor.core.dto.RegisterEventManagerRequest;
import com.conductor.core.model.ResourceType;
import com.conductor.core.model.org.Organization;
import com.conductor.core.model.org.OrganizationPrivilege;
import com.conductor.core.model.permission.AccessLevel;
import com.conductor.core.security.UserPrincipal;
import com.conductor.core.security.fiber.FiberIdentityProvider;
import com.conductor.core.security.fiber.FiberPermissionEvaluatorChain;
import com.conductor.core.service.OrganizationOperatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.PutExchange;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/organizations/operators")
@Validated
@RequiredArgsConstructor
public class OrganizationOperatorController {
    private final OrganizationOperatorService operatorService;
    private final FiberPermissionEvaluatorChain fiberPermissionEvaluatorChain;
    private final FiberIdentityProvider fiberIdentityProvider;


    @PutMapping("/register/events/manager")
    ResponseEntity<?> registerManager(
            @RequestBody
            RegisterEventManagerRequest request,
            Authentication auth){

        String organizationExternalId =
                fiberIdentityProvider.getRootResource(
                        ResourceType.EVENT,
                        request.getEventExternalId());

        fiberPermissionEvaluatorChain
                .isOperator()
                .orPermission(
                        organizationExternalId,
                        ResourceType.ORGANIZATION,
                        Map.of(
                                OrganizationPrivilege.OPERATOR,
                                AccessLevel.WRITE))
                .evaluate(auth);

        operatorService.registerManager(
                (UserPrincipal) auth.getPrincipal(),
                request
        );
        return ResponseEntity.ok().build();
    }
}
