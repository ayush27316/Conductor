package com.conductor.core.model.event;

import com.conductor.core.model.common.AccessLevel;
import com.conductor.core.model.common.Resource;

/**
 * Defines the different system resources within an {@link Event}
 * that can be protected and controlled through access management
 * policies. {@link EventResource} must be combined with {@link AccessLevel}
 * to determine users authority to a rest resource.
 */
public enum EventResource implements Resource {

    /**
     * Represents event operators that might facilitate check-ins, ticket approval etc.
     * <p>
     * Access to this resource controls which users can create, delete or update operators.
     * operators can further be assigned {@link EventPermission 's} for providing authorization
     * in order to  facilitate   check-ins or approval of reservation of this event.
     *</p>
     */
    OPERATORS,

    /**
     * Represents event configuration settings.
     */
    CONFIG,

    /**
     * Represents audit-related information.
     * <p>{@link EventAudit} can only be read. Thereforem, AUDIT resource
     * can only have READ {@link AccessLevel}
     * </p>
     */
    AUDIT,

    /**
     * Represents event members or participants of an event.
     * <p>
     * Access to this resource controls who can
     * view, add, remove, or manage members within
     * the context of an event.
     * </p>
     */
    MEMBER
}
