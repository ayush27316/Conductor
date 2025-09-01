package com.conductor.core.model.permission;

import com.conductor.core.model.common.ResourceType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.Map;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionMap {

    private ResourceType resourceType;
    private Map<Privilege, AccessLevel> permission;

}
