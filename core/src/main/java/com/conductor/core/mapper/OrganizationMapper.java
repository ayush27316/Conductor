package com.conductor.core.mapper;

import com.conductor.core.dto.OrganizationDTO;
import com.conductor.core.model.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {

//   OrganizationMapper INSTANCE = Mappers.getMapper(OrganizationMapper.class);

   @Mapping(target = "externalId", ignore = true) // Will be auto-generated
   @Mapping(target = "events", ignore = true) // Not part of DTO
   @Mapping(target = "audit", ignore = true) // Will be set separately
   Organization toEntity(OrganizationDTO dto);

   OrganizationDTO toDto(Organization entity);
}