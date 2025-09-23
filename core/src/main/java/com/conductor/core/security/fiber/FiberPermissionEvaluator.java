    package com.conductor.core.security.fiber;

    import com.conductor.core.security.PrincipalPermission;
    import com.conductor.core.model.ResourceType;
    import com.conductor.core.model.permission.AccessLevel;
    import com.conductor.core.model.permission.Privilege;
    import com.conductor.core.model.user.UserRole;
    import com.conductor.core.security.UserPrincipal;
    import org.springframework.security.core.Authentication;
    import org.springframework.stereotype.Component;

    import java.util.Map;
    import java.util.Objects;

    @Component
    public class FiberPermissionEvaluator {

        public boolean hasRole(Authentication auth, UserRole role) {
            UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
            return  principal.getRole().equals(role);
        }

        //when targetResourceExternalId is null the we consider that
        public  boolean hasPermission(
                Authentication auth,
                String targetResourceExternalId,
                ResourceType targetType,
                Map<Privilege, AccessLevel> permission)
        {
            UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
            String externalId =  targetResourceExternalId;

            for (PrincipalPermission p : userPrincipal.getPermissions()) {
                if (externalId.equals(p.getResourceExternalId())) {

                    if(Objects.isNull(permission)){
                        return true;
                    }

                    Map<Privilege, AccessLevel> requiredPermission = permission;

                    Map<Privilege, AccessLevel> userPermission = p.getPermissions();
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
