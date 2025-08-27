package com.conductor.core.model.user;

import com.conductor.core.model.common.AccessLevel;
import com.conductor.core.util.Option;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Represents the different categories of users that can interact with conductor.
 */
public enum UserRole {

    /**
     * System administrator with 'WRITE' {@link AccessLevel}
     * to all the resourceType.
     */
    ADMIN("ADMIN"),

    /**
     * Event operator or Organization level operators responsible for tasks such as
     * check-ins, ticket validation, or managing event-related operations. An
     * operators permissions for a resourceType must be fetched from PermissionRegistry
     * of the respective resourceType.
     */
    OPERATOR("OPERATOR"),

    /**
     * Machine-to-machine authenticated identity used for integrations,
     * automation, or external services communicating with the system via APIs.
     */
    API_KEY("API_KEY"),

    /**
     *<p>
     *  USER  has read-only access to resources that are publicly
     *  accessible i.e. resources whose url begins with "/public".
     *  This role is also useful as placeholder for signifying that
     *  only a logged-in user has access to a given  resources.
     * </p>
     */
    USER("USER");

    private final String label;

    UserRole(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static UserRole fromValue(String value) {
        for (UserRole role : values()) {
            if (role.label.equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + value);
    }

    /**
     * @return a comma-separated string of all role options
     */
    public static String getAllOptions() {
        return Arrays.stream(UserRole.values())
                .map(UserRole::getLabel)
                .collect(Collectors.joining(", "));
    }
}
