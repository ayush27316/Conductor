package com.conductor.core.model.user;

import com.conductor.core.model.Resource;
import com.conductor.core.model.ResourceType;
import com.conductor.core.model.org.Organization;
import com.conductor.core.model.permission.Permission;
import com.conductor.core.model.ticket.Ticket;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

/**
 * Represents a system user that can authenticate and interact with the platform.
 * <p>
 * This entity holds authentication credentials, profile information, and
 * an associated {@link UserRole}.
 * </p>
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends Resource implements UserDetails {

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "username", nullable = false, updatable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Size(max = 150, message = "Email address cannot exceed 150 characters")
    @Column(name = "email_address", nullable = false, unique = true, length = 150)
    private String emailAddress;

    @ManyToOne
    @JoinColumn(name = "organization_id_fk")
    private Organization organization;

    @OneToMany(
            mappedBy = "grantedTo",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Permission> permissions;

//    @OneToMany(mappedBy = "submittedBy", fetch = FetchType.LAZY ,cascade = CascadeType.PERSIST)
//    private List<Application> applications = new ArrayList<>();

    @OneToMany(mappedBy = "user",fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private List<Ticket> tickets = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.getName()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @PrePersist
    public void prePersist() {
        super.init(ResourceType.USER, this);

    }
}
