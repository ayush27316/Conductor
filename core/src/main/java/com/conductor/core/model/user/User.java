package com.conductor.core.model.user;

import com.conductor.core.model.permission.BaseEntity;
import com.conductor.core.model.permission.Permission;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Represents a system user that can authenticate and interact with the platform.
 * <p>
 * This entity holds authentication credentials, profile information, and
 * an associated {@link UserType}.
 * </p>
 */
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_username", columnNames = "username"),
                @UniqueConstraint(name = "uk_users_email", columnNames = "email_address")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @NotNull(message = "User type must be specified")
    private String type;

    @Column(name="external_id", unique = true, nullable = false)
    private String externalId;


    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(name = "username", nullable = false, updatable = false, unique = true, length = 50)
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 255, message = "Password must be at least 8 characters long")
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @NotBlank(message = "First name cannot be blank")
    @Size(max = 100, message = "First name cannot exceed 100 characters")
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @NotBlank(message = "Email address cannot be blank")
    @Email(message = "Email address must be valid")
    @Size(max = 150, message = "Email address cannot exceed 150 characters")
    @Column(name = "email_address", nullable = false, unique = true, length = 150)
    private String emailAddress;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Permission> permissions;

    @PrePersist
    public void ensureExternalId() {
        if (externalId == null) {
            externalId = UUID.randomUUID().toString();
        }
    }
}
