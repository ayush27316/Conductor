package com.conductor.core.service;

import com.conductor.core.dto.EventDTO;
import com.conductor.core.dto.EventRegistrationRequest;
import com.conductor.core.exception.EventRegistrationFailedException;
import com.conductor.core.model.event.EventAccessDetails;
import com.conductor.core.model.event.EventStatus;
import com.conductor.core.model.permission.Permission;
import com.conductor.core.model.permission.Resource;
import com.conductor.core.model.user.User;
import com.conductor.core.util.EventMapper;
import com.conductor.core.model.event.Event;
import com.conductor.core.model.org.Organization;

import com.conductor.core.repository.EventRepository;

import com.conductor.core.repository.OrganizationRepository;
import com.conductor.core.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Objects;
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
    public Boolean registerEvent(EventRegistrationRequest request) {

        List<Permission> permissions = getCurrentUser().getPermissions();
        Optional<Organization> org = Optional.empty();
        for(Permission p: permissions){
            if(p.getResourceName().equals(Resource.ORGANIZATION.getName()))
            {
                org = organizationRepository.findByExternalId(p.getResourceId());
            }
        }
        if(org.isEmpty())
        {
            throw new EventRegistrationFailedException("User must be associated with an organization to create events");
        }

        Event event = toEntityFromRegistrationRequest(request);

        event.setOrganization(org.get());
        eventRepository.save(event);

        return true;
    }

    private Event toEntityFromRegistrationRequest(EventRegistrationRequest request) {
        return Event.builder()
                .format(request.getFormat())
                .displayName(request.getName())
                .location(request.getLocation())
                .begin(request.getBegin())
                .end(request.getEnd())
                .accessDetails(EventAccessDetails.builder()
                        .accessStrategy(request.getAccessStrategy())
                        .accessibleFrom(request.getAccessibleFrom())
                        .accessibleTo(request.getAccessibleTo())
                        .build())
                .status(EventStatus.DRAFT.getName())
                .build();
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
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }
}

