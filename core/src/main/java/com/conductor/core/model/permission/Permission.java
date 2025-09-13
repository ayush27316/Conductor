package com.conductor.core.model.permission;

import com.conductor.core.model.BaseEntity;
import com.conductor.core.model.Resource;
import com.conductor.core.model.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a single permission grant for a user on a specific resourceType.
 *
 * Who can change a users' permission. Firs thing is that only operators
 * have permissions.
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
    @JoinColumn(name = "granted_to_id_fk", nullable = false)
    @JsonBackReference
    private User grantedTo;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id_fk", nullable = false)
    private Resource resource;

    @Getter(value = lombok.AccessLevel.NONE)
    @Setter(value = lombok.AccessLevel.NONE)
    @Convert(converter = PermissionConverter.class)
    @Column(name = "privileges", columnDefinition = "CLOB")
    private Map<Privilege, AccessLevel> permission = new HashMap<>();

    @Column(name = "granted_at", nullable = true)
    private LocalDateTime grantedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granted_by_user_id_fk")
    private User grantedBy;

    //never expire if null
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    /**
     *
     * @return An immutable reference to Permissions map. To change the permission
     * you must use {@link #setPermission(Map)} method which replaces the existing map
     * with the provided map.
     */
    public Map<Privilege, AccessLevel> getPermission(){
        return permission.isEmpty() ? Map.copyOf(permission): new HashMap<>();
    }

    public void setPermission(Map<Privilege, AccessLevel> newPermission){
         if(newPermission != null){
             permission = newPermission;
         }
    }

    @PrePersist
    void doPrePersist(){
        if(Objects.isNull(grantedAt)){
            grantedAt = LocalDateTime.now();
        }
    }
}
