package com.conductor.core.model.user;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents the different categories of users that can interact with conductor.
 */
public enum UserRole {

    /**
     * System administrator with 'WRITE' {@link com.conductor.core.model.permission.AccessLevel}
     * to all the resource.
     */
    ADMIN("ADMIN"),

    /**
     * Event operator or Organization level operators responsible for tasks such as
     * check-ins, ticket validation, or managing event-related operations. An
     * operators permissions for a resource must be fetched from PermissionRegistry
     * of the respective resource.
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

    private String name;


    UserRole(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }


    private static final Map<String, UserRole> LOOKUP =
            Stream.of(values()).collect(Collectors.toMap(UserRole::getName, r -> r));

    /**
     * Resolves a user type from its string name.
     *
     * @param name the type name
     * @return an Optional containing the matching type, or empty if not found
     */
    public static Optional<UserRole> fromName(String name) {
        return Optional.ofNullable(LOOKUP.get(name));
    }


}
