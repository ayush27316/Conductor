package com.conductor.core.util;

import com.conductor.core.dto.EventDTO;
import com.conductor.core.dto.permission.PermissionDTO;
import com.conductor.core.model.event.Event;
import com.conductor.core.model.permission.AccessLevel;
import com.conductor.core.model.event.EventPrivilege;
import com.conductor.core.model.org.OrganizationPrivilege;
import com.conductor.core.model.permission.Permission;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for mapping permissions between different formats
 * and handling JSON conversion for database storage
 */

//@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toEntity(PermissionDTO dto);

    PermissionDTO toDto(Permission entity);
}

