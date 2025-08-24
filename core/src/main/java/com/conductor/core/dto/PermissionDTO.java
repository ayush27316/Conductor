package com.conductor.core.dto;

import com.conductor.core.model.common.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDTO {

    private Long id;
    private Long userId;
    private String username;
    private String resource;
    private String resourceId;
    private String privilege;
    private AccessLevel accessLevel;
    private Boolean isActive;
    private ZonedDateTime grantedAt;
    private Long grantedByUserId;
    private String grantedByUsername;
    private ZonedDateTime expiresAt;
    private Map<String, String> metadata;
}
