package com.conductor.core.security;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.conductor.core.model.user.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPrincipal implements UserDetails {
    private Long id;
    private String name;
    private String username;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (user.getType() != null) {
            switch (user.getType()) {
                case PUBLIC:
                    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                    break;
                case OPERATOR:
                    authorities.add(new SimpleGrantedAuthority("ROLE_OPERATOR"));
                    // TODO: Add operator-specific authorities based on roles
                    break;
                case ADMIN:
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                    break;
                case API_KEY:
                    authorities.add(new SimpleGrantedAuthority("ROLE_API"));
                    break;
                default:
                    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                    break;
            }
        } else {
            // Default authority if user type is not set
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return UserPrincipal.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmailAddress())
                .authorities(authorities)
                .build();
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPrincipal that = (UserPrincipal) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}