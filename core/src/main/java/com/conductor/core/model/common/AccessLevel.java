package com.conductor.core.model.common;

/**
 * Defines the types of actions that can be granted
 * on a given {@link Resource}.
 * <p>
 * These access levels allow fine-grained control
 * over what a user can perform within the scope
 * of a protected resource.
 */
public enum AccessLevel {

    /**
     * Grants read-only access to a resource.
     * <p>
     * Users with this level can view data but
     * cannot modify, delete, or create records.
     */
    READ,

    /**
     * Grants permission to create or update a resource.
     * <p>
     * Users with this level can modify existing data
     * or insert new records within the resource.
     */
    WRITE,

    /**
     * Grants permission to remove or revoke a resource.
     * <p>
     * Users with this level can delete data entries
     * or revoke access tied to the resource.
     */
    DELETE,

    /**
     * Grants full access to the resource.
     * <p>
     * Equivalent to combining {@link #READ},
     * {@link #WRITE}, and {@link #DELETE}.
     */
    ALL
}
