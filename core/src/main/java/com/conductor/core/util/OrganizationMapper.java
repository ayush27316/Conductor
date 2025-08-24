package com.conductor.core.util;

import com.conductor.core.dto.OrganizationDTO;
import com.conductor.core.model.org.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {
    //@Mapping(target = "events", ignore = true)
    Organization toEntity(OrganizationDTO dto);

    OrganizationDTO toDto(Organization entity);
}