package com.conductor.core.model.org;

import com.conductor.core.model.event.EventPrivilege;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Defines the different privileges that an {@link Organization}
 * can grant to its operators. These privileges control access to
 * organization-level resources and operations.
 */
public enum OrganizationPrivilege {

    /**
     * Grants access to creating and managing operators (users) for an organization.
     * <p>
     * Typically, only the owner of the organization has
     * OPERATORS privilege. Operators can be granted access to
     * other privileges within an organization like EVENT_MANAGEMENT, CONFIG.
     * Owner Operator can grant access to OPERATORS to other operators.
     * Therefore, this privilege must be given to trusted users.
     * </p>
     */
    OPERATOR("operator"),

    /**
     * Grants the ability to create, manage, and monitor events
     * associated with the organization. This is different from
     * event-specific privileges - it controls the ability to
     * create and manage events at the organization level. This
     * is equivalent of giving all privileges of EVENT resourse
     * with 'ALL' AccessLevel.
     */
    EVENT("event"),

    /**
     * Grants access to audit logs and monitoring tools, allowing
     * tracking of organizational activity.
     */
    AUDIT("audit"),

    /**
     * Grants access to configuration settings at the organization level,
     * such as changing organization name, description, email etc.
     **/
    CONFIG("config"),

    /**
     * Grants access to view organization information and basic details.
     * By default, 'PUBLIC' users are granted 'VIEW' access.
     *
     * [?]: For things that organizations are to be public can be placed
     * under /public path so we may not need view entirely. It should be
     * customizable that what user can see. For example maybe an organization
     * want to show certain metrics like total tickets sold which is part of
     * AUDIT privilege.
     */
    VIEW("view");

    private String name;
    OrganizationPrivilege(String name)
    {
        this.name = name;
    };

    public String getName(){
        return this.name;
    }

    private static final Map<String, OrganizationPrivilege> LOOKUP =
            Stream.of(values()).collect(Collectors.toMap(OrganizationPrivilege::getName, r -> r));

    /**
     * Resolves a resource from its string name.
     *
     * @param name the resource name
     * @return an Optional containing the matching Resource, or empty if not found
     */
    public static Optional<OrganizationPrivilege> fromName(String name) {
        return Optional.ofNullable(LOOKUP.get(name));
    }

}
