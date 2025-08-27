package com.conductor.core.service;

import com.conductor.core.dto.EventDTO;
import com.conductor.core.exception.EventRegistrationFailedException;
import com.conductor.core.model.event.*;
import com.conductor.core.model.permission.Permission;
import com.conductor.core.model.common.ResourceType;
import com.conductor.core.model.user.User;
import com.conductor.core.model.org.Organization;

import com.conductor.core.repository.EventRepository;

import com.conductor.core.repository.OrganizationRepository;
import com.conductor.core.repository.UserRepository;
import com.conductor.core.util.EventMapper;
import com.conductor.core.util.OptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;

    @Transactional
    public Boolean registerEvent(EventDTO request) {

        List<Permission> permissions = getCurrentUser().getPermissions();
        Optional<Organization> org = Optional.empty();
        for(Permission p: permissions){
            if(p.getResource().getResourceType().getLabel().equals(ResourceType.ORGANIZATION.getLabel()))
            {
                org = Optional.of((Organization) p.getResource());
            }
        }
        if(org.isEmpty())
        {
            throw new EventRegistrationFailedException("User must be associated with an organization to create events");
        }

        Event event = eventMapper.toEntity(request);

        event.setOrganization(org.get());
        eventRepository.save(event);

        return true;
    }

    public User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            throw new SecurityException("Invalid authentication context");
        }

        return  (User) authentication.getPrincipal();
    }

    public List<EventDTO> getAllEvents() {

       return eventRepository.findAll().stream()
                .map(eventMapper::toDTO)
                .collect(Collectors.toList());
    }
}

