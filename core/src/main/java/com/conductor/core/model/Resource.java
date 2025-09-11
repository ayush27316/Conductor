package com.conductor.core.model;

import com.conductor.core.model.application.Application;
import com.conductor.core.model.event.Event;
import com.conductor.core.model.org.Organization;
import com.conductor.core.model.permission.Permission;
import com.conductor.core.model.user.User;
import jakarta.persistence.*;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link Resource} is an entity for which {@link Permission}
 * can be granted to a {@link User}. Extending {@link Event}, {@link Organization}
 * entities among other allows for polymorphic association between entities.
 * @see  Application
 * @see  Permission
 */
@Entity
@Table(
        indexes = {
                @Index(name = "idx_resource_id", columnList = "external_id")
        }
)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Resource extends BaseEntity {


    /**
     * {@code externalId} is meant to be used as a globally unique reference
     * to this resource.
     */
    @Column(name = "external_id",
            unique = true,
            updatable = false,
            nullable = false)
    private String externalId;

    @Column(name = "resource_type",
            updatable = false,
            nullable = false)
    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;

    /**
     * Provides an external identity to this entity on calling {@link #init(ResourceType, Object)}.
     * @see {@link ExternalIdentityProvider}.
     */
    @Transient
    private ExternalIdentityProvider externalIdentityProvider;

    /**
     * Each resource entity that extends {@link Resource}  must call {@code super.init}
     * with appropriate resource type before persisting. This also sets a globally unique id that
     * should be used to refer to this resource.
     *
     */
    public void init(ResourceType resourceType, Object info){
        if(Objects.isNull(externalIdentityProvider)) {
            externalIdentityProvider = ExternalIdentityProviderContainer.get();
        }
        if(Objects.isNull(externalId)){
            externalId = externalIdentityProvider.generateId(resourceType, info);
        }
        this.resourceType = resourceType;
    }

    public ResourceType getResourceType()
    {
        return this.resourceType;
    }

    /**
     * @return a globally unique id that can be used to refer to this resource.
     */
    public String getExternalId()
    {
        return this.externalId;
    }

    /**
     * Cast a {@link Resource} entity to a derived resource entity (eg:, {@link Event}.
     * {@link Organization}, etc.). A resource entity when injected from other objects
     * might be proxied due to lazy associations between different entities. safeCast unproxies
     * the entity if necessary and then do a normal cast to the targetResource type.
     *
     * @param targetResource a subclass of {@link Resource} that is the target of this cast.
     * @param source source {@link Resource} object
     * @return An optional object of type  {@code targetResource}.
     *
     */
    @SuppressWarnings("unchecked")
    public static <E extends Resource> Optional<E> safeCast(Class<E> targetResource, Resource source) {
        if (source == null) {
            return Optional.empty();
        }

        // Unwrap Hibernate proxy if necessary
        Object unproxied = (source instanceof HibernateProxy)
                ? Hibernate.unproxy(source)
                : source;

        // Type check
        if (targetResource.isInstance(unproxied)) {
            return Optional.of((E) unproxied);
        } else {
            return Optional.empty();
        }
    }
}
