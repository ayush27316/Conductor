package com.conductor.core.service;

import com.conductor.core.dto.event.EventModification;
import com.conductor.core.exception.*;
import com.conductor.core.model.event.*;
import com.conductor.core.model.org.Organization;
import com.conductor.core.model.user.Operator;
import com.conductor.core.model.user.User;
import com.conductor.core.repository.*;
import com.conductor.core.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.util.Assert.isTrue;
import static  org.springframework.util.Assert.notNull;

import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class EventRegistrationAndModificationService {

    private final EventRepository eventRepository;
    private final OperatorRepository operatorRepository;
    private final ResourceAuditRepository resourceAuditRepository;

    @Transactional
    public String registerEvent(
            User user,
            EventModification request) {

        notNull(request.getName(), "Event name is required");
        notNull(request.getFormat(), "Event format is required");
        notNull(request.getLocation(), "Event location is required");
        notNull(request.getBegin(), "Begin time is required");
        notNull(request.getEnd(), "End time is required");
        isTrue(request.getTotalTicketsToBeSold() > 0, "Total tickets to be sold must be greater than zero");
        notNull(request.getAccessStrategy(), "Access strategy is required");
        notNull(request.getAccessibleFrom(), "Accessible from date is required");
        notNull(request.getAccessibleTo(), "Accessible to date is required");
        notNull(request.getDescription(), "Event description is required");

        isTrue(request.getEnd().isAfter(request.getBegin()), "End time must be after begin time");
        isTrue(request.getAccessibleTo().isAfter(request.getAccessibleFrom()), "Accessible-to must be after accessible-from");
        if (!request.isFree()) {
            notNull(request.getTicketPrice(), "Ticket price is required when event is not free");
//            Assert.notNull(currency, "Currency is required when event is not free");
          }

        Operator operator = operatorRepository.findByUser_ExternalId(user.getExternalId())
                .orElseThrow(() -> new OperatorNotFoundException(user.getId()));

        Organization org = operator.getOrganization();

        Event event = modificationToEvent(request);
        event.setOrganization(org);

        event = eventRepository.save(event);
      //        ResourceAudit audit = ResourceAudit.builder()
//                .createdAt(LocalDateTime.now())
//                .createdBy(user)
//                .resource((Resource) event)
//                .build();
//
        //        resourceAuditRepository.save(audit);

        return event.getExternalId();
    }

    @Transactional
    public void modifyEvent(
            User user,
            String eventExternalId,
            EventModification request)
    {
        Event event = eventRepository.findByExternalId(eventExternalId)
                .orElseThrow(() -> new EventNotFoundException());

        applyModification(event, request);
//
//        resourceAuditRepository.save(ResourceAudit.forUpdate(event, user));
//
        eventRepository.save(event);
    }

    private void applyModification(Event event, EventModification em) {

        Utils.updateIfNotNull(event::setName, em.getName());
        Utils.updateIfNotNull(event::setFormat, em.getFormat());
        Utils.updateIfNotNull(event::setLocation, em.getLocation());
        Utils.updateIfNotNull(event::setBegin, em.getBegin());
        Utils.updateIfNotNull(event::setEnd, em.getEnd());
        Utils.updateIfNotNull(event::setDescription, em.getDescription());

        if (em.getTotalTicketsToBeSold() > 0) {
            event.setTotalTicketsToBeSold(em.getTotalTicketsToBeSold());
        }
        Utils.updateIfNotEmpty(event::setOptions, em.getOptions());

        updateAccessDetails(event, em);
        updatePaymentDetails(event, em);
    }

    private void updateAccessDetails(Event event, EventModification em) {
        // return if there are no access related modification
        if (em.getAccessStrategy() == null && em.getAccessibleFrom() == null && em.getAccessibleTo() == null) {
            return;
        }

        // Get existing details or create a new instance if it's null
        EventAccessDetails details = Optional.ofNullable(event.getAccessDetails())
                .orElseGet(EventAccessDetails::new);

        Utils.updateIfNotNull(details::setAccessStrategy, em.getAccessStrategy());
        Utils.updateIfNotNull(details::setAccessibleFrom, em.getAccessibleFrom());
        Utils.updateIfNotNull(details::setAccessibleTo, em.getAccessibleTo());

        //verify change
        isTrue(em.getAccessibleTo().isAfter(em.getAccessibleFrom()), "Accessible-to must be after accessible-from");

        event.setAccessDetails(details);
    }

    private void updatePaymentDetails(Event event, EventModification em) {
        EventPaymentDetails details = Optional.ofNullable(event.getPaymentDetails())
                .orElseGet(EventPaymentDetails::new);

        details.setIsfree(em.isFree());
        if (em.isFree()) {
            details.setTicketPrice(null);
            details.setCurrency(null);
        } else {
            Utils.updateIfNotNull(details::setTicketPrice, em.getTicketPrice());
            Utils.updateIfNotNull(details::setCurrency, em.getCurrency());
        }
        event.setPaymentDetails(details);
    }

    private Event modificationToEvent(EventModification em)
    {
        return Event.builder()
                .format(em.getFormat())
                .name(em.getName())
                .location(em.getLocation())
                .begin(em.getBegin())
                .end(em.getEnd())
                .totalTicketsToBeSold(em.getTotalTicketsToBeSold())
                .accessDetails(EventAccessDetails.builder()
                        .accessStrategy(em.getAccessStrategy())
                        .accessibleFrom(em.getAccessibleFrom())
                        .accessibleTo(em.getAccessibleTo())
                        .build())
                .paymentDetails(EventPaymentDetails.builder()
                        .isfree(em.isFree())
                        .ticketPrice(em.isFree()? null : em.getTicketPrice())
                        .currency(em.isFree()? null : em.getCurrency())
                        .build())
                .description(em.getDescription())
                .status(EventStatus.DRAFT)
                .options(em.getOptions())
                .build();
    }
}
