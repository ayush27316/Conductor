package com.conductor.core.service;

import com.conductor.core.dto.EventDTO;
import com.conductor.core.util.EventMapper;
import com.conductor.core.model.event.Event;
import com.conductor.core.model.org.Organization;

import com.conductor.core.repository.EventRepository;

import com.conductor.core.repository.OrganizationRepository;
import com.conductor.core.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    @Autowired
    private EventMapper eventMapper;

    //we can create annotation like @SecureEntryWithRoles({ADMIN, API_KEY})
    //for reducing repeated code
    @Transactional
    public void registerEvent(EventDTO dto, String organizationName) {
        Event event = eventMapper.toEntity(dto);
        Organization organization = organizationRepository.findByName(organizationName)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));
        event.setOrganization(organization);
        eventRepository.save(event);
    }


    public List<EventDTO> getAllEvents() {

       return eventRepository.findAll().stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }


}

