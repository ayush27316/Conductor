package com.conductor.core.service;

import com.conductor.core.dto.EventDTO;
import com.conductor.core.dto.event.EventRegistrationRequest;
import com.conductor.core.exception.EventRegistrationFailedException;
import com.conductor.core.exception.OrganizationNotFound;
import com.conductor.core.model.audit.ResourceAudit;
import com.conductor.core.model.common.Resource;
import com.conductor.core.model.event.*;
import com.conductor.core.model.permission.Permission;
import com.conductor.core.model.common.ResourceType;
import com.conductor.core.model.user.User;
import com.conductor.core.model.org.Organization;

import com.conductor.core.repository.EventRepository;

import com.conductor.core.repository.OrganizationRepository;
import com.conductor.core.repository.ResourceAuditRepository;
import com.conductor.core.repository.UserRepository;
import com.conductor.core.util.EventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventRegistrationService{

    private final EventRepository eventRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final ResourceAuditRepository resourceAuditRepository;

    @Transactional
    public boolean registerEvent(Authentication auth,
                                 EventRegistrationRequest request) {

        Organization org = organizationRepository.findByExternalId(request.getOrganizationId())
                .orElseThrow(()-> new OrganizationNotFound());

        Event event = eventEntityFromRegistrationRequest(request);
        event.setOrganization(org);
        ResourceAudit audit = ResourceAudit.builder()
                .createdAt(LocalDateTime.now())
                .createdBy((User) auth.getPrincipal())
                .resource((Resource) event)
                .build();

        eventRepository.save(event);
        resourceAuditRepository.save(audit);
        return true;
    }


    private Event eventEntityFromRegistrationRequest(EventRegistrationRequest request) {
        return Event.builder()
                .format(request.getFormat())
                .name(request.getName())
                .location(request.getLocation())
                .begin(request.getBegin())
                .end(request.getEnd())
                .totalTicketsToBeSold(request.getTotalTicketsToBeSold())
                .accessDetails(EventAccessDetails.builder()
                        .accessStrategy(request.getAccessStrategy())
                        .accessibleFrom(request.getAccessibleFrom())
                        .accessibleTo(request.getAccessibleTo())
                        .build())
                .paymentDetails(EventPaymentDetails.builder()
                        .isfree(request.isFree())
                        .ticketPrice(request.isFree()? null : request.getTicketPrice())
                        .currency(request.isFree()? null : request.getCurrency())
                        .build())
                .description(request.getDescription())
                .status(EventStatus.DRAFT)
                .build();

    }
}

