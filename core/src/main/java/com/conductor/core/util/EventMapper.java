package com.conductor.core.util;

import com.conductor.core.dto.EventDTO;
import com.conductor.core.model.event.*;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public Event toEntity(EventDTO dto) {
        return Event.builder()
                .format(dto.getFormat())
                .name(dto.getName())
                .location(dto.getLocation())
                .begin(dto.getBegin())
                .end(dto.getEnd())
                .totalTicketsToBeSold(dto.getTotalTicketsToBeSold())
                .accessDetails(EventAccessDetails.builder()
                        .accessStrategy(dto.getAccessStrategy())
                        .accessibleFrom(dto.getAccessibleFrom())
                        .accessibleTo(dto.getAccessibleTo())
                        .build())
                .description(dto.getDescription())
                .status(EventStatus.DRAFT)
                .build();
    }

    public EventDTO toDTO(Event event) {
        return EventDTO.builder()
                .id(event.getExternalId().toString())
                .name(event.getName())
                .format(event.getFormat())
                .location(event.getLocation())
                .begin(event.getBegin())
                .end(event.getEnd())
                .totalTicketsToBeSold(event.getTotalTicketsToBeSold())
                .accessStrategy(event.getAccessDetails().getAccessStrategy())
                .accessibleFrom(event.getAccessDetails().getAccessibleFrom())
                .accessibleTo(event.getAccessDetails().getAccessibleTo())
                .description(event.getDescription())
                .build();
    }


}