package com.conductor.core.model.permission;

import com.conductor.core.model.user.User;
import com.conductor.core.util.PermissionMapConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Represents a single permission grant for a user on a specific resource.
 * This is the atomic unit of permission in the system.
 */
@Entity
@Table(name = "user_permissions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission extends BaseEntity {

    /**
     * The user this permission is granted to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;


    /*
    * external_id on resources (organization, events...)
    * are mapped to resourceID here
    * */
    @Column(name = "resource_id", nullable = false)
    private String resourceId;

    /**
     * The resource this permission applies to
     */
    @Column(name = "resource_name", nullable = false)
    private String resourceName;

    /**
     * Map of privileges to access levels for this resource
     * Key: privilege name, Value: access level
     */
    @Convert(converter = PermissionMapConverter.class)
    @Column(name = "privileges", columnDefinition = "JSON")
    private Map<String, String> privileges;

    @Column(name = "granted_at", nullable = true)
    private ZonedDateTime grantedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "granted_by_user_id_fk")
    private User grantedBy;

    @Column(name = "expires_at")
    private ZonedDateTime expiresAt;

}
