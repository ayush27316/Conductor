package com.conductor.core.model.org;

import com.conductor.core.model.common.AccessLevel;
import com.conductor.core.util.Option;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

    private final String label;

    OrganizationPrivilege(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static OrganizationPrivilege fromValue(String value) {
        for (OrganizationPrivilege privilege : values()) {
            if (privilege.label.equalsIgnoreCase(value)) {
                return privilege;
            }
        }
        throw new IllegalArgumentException("Unknown organization privilege: " + value);
    }

    /**
     * @return a comma-separated string of all organization privilege options
     */
    public static String getAllOptions() {
        return Arrays.stream(OrganizationPrivilege.values())
                .map(OrganizationPrivilege::getLabel)
                .collect(Collectors.joining(", "));
    }

    public static Map<String,String> getOwnerPrivileges(){
        Map<String, String> privileges = new HashMap<>();

        privileges.put(EVENT.getLabel(), AccessLevel.WRITE.getLabel());
        privileges.put(OPERATOR.getLabel(), AccessLevel.WRITE.getLabel());
        privileges.put(CONFIG.getLabel(), AccessLevel.WRITE.getLabel());
        privileges.put(AUDIT.getLabel(), AccessLevel.READ.getLabel());

        return privileges;
    }

}
