package com.conductor.core.model.common;

import com.conductor.core.model.permission.Privilege;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


/**
 * A {@link Resource} is an entity for which {@link com.conductor.core.model.permission.Permission}
 * can be granted to a {@link com.conductor.core.model.user.User}.
 */

@Entity
@Table(
        indexes = {
                @Index(name = "idx_resource_guid", columnList = "external_guid")
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
     * Each resource entity that extends {@link Resource}  must call {@code super.init}
     * with appropriate resource type before persisting. This also sets a globally unique id that
     * should be used to refer to this resource.
     *
     */
    public void init(ResourceType resourceType){
        if(Objects.isNull(externalId)){
            externalId = UUID.randomUUID().toString();
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
//
//    //safeCast
//    static <E extends Resource> Optional<E> safeCast(Class<E> targetResource, Resource source) {
//        return source;
//    }
//
}
