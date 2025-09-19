package com.conductor.core.security.fiber;

import com.conductor.core.model.ResourceType;
import com.conductor.core.model.permission.AccessLevel;
import com.conductor.core.model.permission.Privilege;
import com.conductor.core.model.user.UserRole;
import com.conductor.core.security.UserPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Component
public class FiberPermissionEvaluatorChain {

    private final FiberPermissionEvaluator permissionEvaluator;
    private final List<PermissionCheck> checks = new ArrayList<>();
    private boolean allowAdminByDefault = true;
    private boolean adminExplicitlyDenied = false;
    private boolean isOperator = false;

    public FiberPermissionEvaluatorChain(FiberPermissionEvaluator permissionEvaluator) {
        this.permissionEvaluator = permissionEvaluator;
    }

    // Factory method to create a new chain
    public static FiberPermissionEvaluatorChain create(FiberPermissionEvaluator evaluator) {
        return new FiberPermissionEvaluatorChain(evaluator);
    }

    // AND operations
    public FiberPermissionEvaluatorChain add(PermissionCheckBuilder checkBuilder) {
        checks.add(new PermissionCheck(checkBuilder, LogicalOperator.AND));
        return this;
    }

    // OR operations
    public FiberPermissionEvaluatorChain or(PermissionCheckBuilder checkBuilder) {
        checks.add(new PermissionCheck(checkBuilder, LogicalOperator.OR));
        return this;
    }

    // Convenience methods for common checks
    public FiberPermissionEvaluatorChain addRole(UserRole role) {
        return add(auth -> permissionEvaluator.hasRole(auth, role));
    }

    public FiberPermissionEvaluatorChain orRole(UserRole role) {
        return or(auth -> permissionEvaluator.hasRole(auth, role));
    }

    public FiberPermissionEvaluatorChain addPermission(String targetResourceExternalId,
                                                       ResourceType targetType,
                                                       Map<Privilege, AccessLevel> permission) {
        return add(auth -> permissionEvaluator.hasPermission(auth, targetResourceExternalId, targetType, permission));
    }

    public FiberPermissionEvaluatorChain orPermission(String targetResourceExternalId,
                                                      ResourceType targetType,
                                                      Map<Privilege, AccessLevel> permission) {
        return or(auth -> permissionEvaluator.hasPermission(auth, targetResourceExternalId, targetType, permission));
    }

    // Control admin behavior
    public FiberPermissionEvaluatorChain denyAdmin() {
        this.adminExplicitlyDenied = true;
        this.allowAdminByDefault = false;
        return this;
    }

    public FiberPermissionEvaluatorChain disableAdminBypass() {
        this.allowAdminByDefault = false;
        return this;
    }

    // Group operations for complex logic
    public FiberPermissionEvaluatorChain addGroup(FiberPermissionEvaluatorChain subChain) {
        return add(subChain::evaluateInternal);
    }

    public FiberPermissionEvaluatorChain orGroup(FiberPermissionEvaluatorChain subChain) {
        return or(subChain::evaluateInternal);
    }

    /**
     * Evaluates all permission checks in the chain
     * @param auth Authentication object
     * @return true if all checks pass, throws exception otherwise
     * @throws AccessDeniedException if permission is denied
     */
    public boolean evaluate(Authentication auth) throws AccessDeniedException {
        if (evaluateInternal(auth)) {
            return true;
        }
        throw new AccessDeniedException("Access denied: Permission evaluation failed");
    }

    /**
     * Internal evaluation method that returns boolean without throwing exception
     */
    private boolean evaluateInternal(Authentication auth) {
        // Check admin bypass first (unless explicitly denied)
        if (allowAdminByDefault && !adminExplicitlyDenied && isAdmin(auth)) {
            return true;
        }

        if (checks.isEmpty()) {
            return true; // No checks means allow (or rely on admin check above)
        }

        // Process checks with proper AND/OR logic
        return evaluateChecks(auth);
    }

    private boolean evaluateChecks(Authentication auth) {
        boolean result = true; // Start with true for AND logic
        boolean hasOrGroup = false;
        boolean orGroupResult = false;

        for (int i = 0; i < checks.size(); i++) {
            PermissionCheck check = checks.get(i);
            boolean checkResult = check.checkBuilder.check(auth);

            if (check.operator == LogicalOperator.AND) {
                // If we were in an OR group, finalize it first
                if (hasOrGroup) {
                    result = result && orGroupResult;
                    hasOrGroup = false;
                    orGroupResult = false;
                }
                // AND operation
                result = result && checkResult;

            } else { // OR operation
                if (!hasOrGroup) {
                    hasOrGroup = true;
                    orGroupResult = checkResult;
                } else {
                    orGroupResult = orGroupResult || checkResult;
                }
            }
        }

        // Handle final OR group if exists
        if (hasOrGroup) {
            result = result && orGroupResult;
        }

        return result;
    }

    private boolean isAdmin(Authentication auth) {
        try {
            UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
            return UserRole.ADMIN.equals(principal.getRole());
        } catch (ClassCastException e) {
            return false;
        }
    }

    // Clear the chain for reuse
    public FiberPermissionEvaluatorChain clear() {
        checks.clear();
        allowAdminByDefault = true;
        adminExplicitlyDenied = false;
        return this;
    }

    public FiberPermissionEvaluatorChain isOperator() {
        this.isOperator = true;
        return this;
    }

    // Functional interface for permission checks
    @FunctionalInterface
    public interface PermissionCheckBuilder {
        boolean check(Authentication auth);
    }

    // Internal classes
    private static class PermissionCheck {
        final PermissionCheckBuilder checkBuilder;
        final LogicalOperator operator;

        PermissionCheck(PermissionCheckBuilder checkBuilder, LogicalOperator operator) {
            this.checkBuilder = checkBuilder;
            this.operator = operator;
        }
    }

    private enum LogicalOperator {
        AND, OR
    }
}