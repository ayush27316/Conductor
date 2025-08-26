package com.conductor.core.util;

import com.conductor.core.dto.EventDTO;
import com.conductor.core.model.event.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

//@Mapper(componentModel = "spring")
public interface EventMapper {
    //@Mapping(target = "organization", ignore = true)
    Event toEntity(EventDTO dto);

    EventDTO toDto(Event entity);
}