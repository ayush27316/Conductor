package com.conductor.core.model.user;

import com.conductor.core.model.Option;
import com.conductor.core.model.permission.AccessLevel;

/**
 * Represents the different categories of users that can interact with conductor.
 */
public enum UserRole implements Option {

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
     *  USER  has read-only access to resources that are does not
     *  authorization. This role is also useful as placeholder for signifying that
     *  only a logged-in user has access to a given  resources.
     * </p>
     */
    USER("USER"),

    /**
     *<p>
     *  PUBLIC  has read-only access to resources that are publicly
     *  accessible i.e. resources whose url begins with "/public.."
     * </p>
     */
    PUBLIC("PUBLIC");

    private final String name;

    UserRole(String name) {
        this.name = name;
    }


    @Override
    public String getName() {
        return this.name;
    }
}
