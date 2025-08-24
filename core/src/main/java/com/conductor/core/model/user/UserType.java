package com.conductor.core.model.user;

/**
 * Represents the different categories of users that can interact with conductor.
 */
public enum UserType {

    /**
     * System administrator with 'ALL' {@link com.conductor.core.model.common.AccessLevel}
     * to all the resource.
     */
    ADMIN,

    /**
     * Event operator or Organization level operators responsible for tasks such as
     * check-ins, ticket validation, or managing event-related operations. An
     * operators permissions for a resource must be fetched from PermissionRegistry
     * of the respective resource.
     */
    OPERATOR,

    /**
     * Machine-to-machine authenticated identity used for integrations,
     * automation, or external services communicating with the system via APIs.
     */
    API_KEY,

    /**
     * Public user  has read-only access to resources that are publicly
     * accessible i.e. resources whose url begins with "/public".
     * </p>
     */
    PUBLIC
}
