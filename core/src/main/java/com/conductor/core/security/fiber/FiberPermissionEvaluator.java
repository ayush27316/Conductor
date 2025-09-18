package com.conductor.core.security.fiber;

import com.conductor.core.model.ResourceType;
import com.conductor.core.model.permission.AccessLevel;
import com.conductor.core.model.permission.Permission;
import com.conductor.core.model.permission.Privilege;
import com.conductor.core.model.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
public class FiberPermissionEvaluator {

    public  boolean hasPermission(
            Authentication auth,
            String targetResourceExternalId,
            ResourceType targetType,
            Map<Privilege, AccessLevel> permission)
    {
        User userPrincipal = (User) auth.getPrincipal();
        String externalId =  targetResourceExternalId;

        for (Permission p : userPrincipal.getPermissions()) {
            if (p.getResource() != null && externalId.equals(String.valueOf(p.getResource().getExternalId()))) {

                if(Objects.isNull(permission)){
                    return true;
                }

                Map<Privilege, AccessLevel> requiredPermission = permission;

                Map<Privilege, AccessLevel> userPermission = p.getPermission();
                if (userPermission == null) continue;

                boolean allMatched = true;
                for (Map.Entry<Privilege, AccessLevel> required : requiredPermission.entrySet()) {
                    boolean matched = userPermission.entrySet().stream()
                            .anyMatch(e -> e.getKey().equals(required.getKey())
                                    && e.getValue().equals(required.getValue()));

                    if (!matched) {
                        allMatched = false;
                        break;
                    }
                }
                return allMatched; // found resource → return immediately
            }
        }
        return false; // no matching resource found
    }
}
