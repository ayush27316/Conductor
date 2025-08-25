package com.conductor.core.security;

import com.conductor.core.dto.permission.PermissionDTO;
import com.conductor.core.model.permission.Permission;
import com.conductor.core.model.user.UserType;
import com.conductor.core.util.PermissionMapper;
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
    private String externalId;
    private String name;
    private String username;
    private String email;
    private String password;
    private String userType;
    private List<PermissionDTO> permissions;

    public static UserPrincipal create(User user) {
        return UserPrincipal.builder()
                .id(user.getId())
                .externalId(user.getExternalId())
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmailAddress())
                .userType(user.getType())
                .permissions(PermissionMapper.toPermissionDTOs(user.getPermissions()))
                .build();
    }

    public Long getId() {
        return id;
    }

    public String getExternalId(){
        return externalId;
    }

    public String getUserType() {
        return this.userType;
    }

    public List<PermissionDTO> getPermissions() {
        return this.permissions;
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
        return null;
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