package com.conductor.core.model.user;

import com.conductor.core.model.permission.Resource;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents the different categories of users that can interact with conductor.
 */
public enum UserType {

    /**
     * System administrator with 'ALL' {@link com.conductor.core.model.permission.AccessLevel}
     * to all the resource.
     */
    ADMIN("admin"),

    /**
     * Event operator or Organization level operators responsible for tasks such as
     * check-ins, ticket validation, or managing event-related operations. An
     * operators permissions for a resource must be fetched from PermissionRegistry
     * of the respective resource.
     */
    OPERATOR("operator"),

    /**
     * Machine-to-machine authenticated identity used for integrations,
     * automation, or external services communicating with the system via APIs.
     */
    API_KEY("api_key"),

    /**
     * Public user  has read-only access to resources that are publicly
     * accessible i.e. resources whose url begins with "/public".
     * </p>
     */
    PUBLIC("public");

    private String name;


    UserType(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }


    private static final Map<String, UserType> LOOKUP =
            Stream.of(values()).collect(Collectors.toMap(UserType::getName, r -> r));

    /**
     * Resolves a user type from its string name.
     *
     * @param name the type name
     * @return an Optional containing the matching type, or empty if not found
     */
    public static Optional<UserType> fromName(String name) {
        return Optional.ofNullable(LOOKUP.get(name));
    }


}
