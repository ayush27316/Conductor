package com.conductor.core.model.permission;

import com.conductor.core.model.common.BaseEntity;
import com.conductor.core.model.common.Resource;
import com.conductor.core.model.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Represents a single permission grant for a user on a specific resourceType.
 *
 * Who can change a users permission. Firs thing is that only operators
 * have persmissions.
 */
@Entity
@Table(name = "user_permissions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id_fk", "resource_id_fk"})
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission extends BaseEntity {

    /**
     * The user this permission is granted to
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_fk", nullable = false)
    @JsonBackReference
    private User user;


    @ManyToOne
    @JoinColumn(name = "resource_id_fk", nullable = false)
    private Resource resource;

    /**
     * Map of privileges to access levels for this resourceType
     * Key: privilege name, Value: access level
     */
    //the tagetResourceType must be set otherwise converter will fail
    @Embedded
    @Lob
    @Column(name = "permissions", columnDefinition = "CLOB")
    @Convert(converter = PermissionConverter.class)
    private PermissionMap permissionMap;

//    @Convert(converter = PermissionConverter.class)
//    @Column(name = "privileges", columnDefinition = "CLOB")
//    private Map<Privilege, AccessLevel> permission;

    @Column(name = "granted_at", nullable = true)
    private ZonedDateTime grantedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "granted_by_user_id_fk")
    private User grantedBy;

    @Column(name = "expires_at")
    private ZonedDateTime expiresAt;

}
