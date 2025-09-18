package com.conductor.core.model;

import com.conductor.core.security.fiber.FiberIdentityProvider;

/**
 * Responsible for generating a globally unique identifier for all
 * {@link Resource}'s within Conductor. At runtime a Resource will look
 * for a bean that extends {@link ExternalIdentityProvider} and call
 * {@link #generateId(ResourceType type, Object info)} method to generate an external id.
 * Arguments to this method are hints to the provider by the
 * derived resource entities to generate an id based on certain parameters
 * unique to that entity. This way of providing an identity is heavily used
 * Within Conductor's fiber technology used for security and permissions.
 *
 * If no bean is found {@link DefaultExternalIdentityProvider} is used,
 * which is a random UUID generator.
 * @note The generated id is different from primary key. Although, external
 *       is indexed for fast lookups.
 * @see  Resource#init(Resource) 
 * @see  FiberIdentityProvider
 */
public interface ExternalIdentityProvider {
    /**
     * Generates a globally unique id.
     * @param resource target resource
     */
    String generateId(Resource resource );
}
