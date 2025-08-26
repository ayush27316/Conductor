package com.conductor.core.model.common;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


/**
 * A common wrapper for all resources within the system.
 */
@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Resource extends BaseEntity {

    @Column(name = "resource_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;

}
