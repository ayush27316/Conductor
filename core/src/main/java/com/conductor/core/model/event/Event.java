package com.conductor.core.model.event;

import com.conductor.core.model.Resource;
import com.conductor.core.model.ResourceType;
import com.conductor.core.model.form.Form;
import com.conductor.core.model.org.Organization;
import com.conductor.core.model.org.OrganizationPrivilege;
import com.conductor.core.model.permission.AccessLevel;
import com.conductor.core.model.permission.Privilege;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "events")
public class Event extends Resource {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventFormat format;

    @Column(nullable = false, length = 100)
    private String name;

    @Column
    private String location;

    @Column(name = "begin_time", nullable = false)
    private LocalDateTime begin;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime end;

    @Embedded
    private EventAccessDetails accessDetails;

    private LocalDateTime applicationOpen;
    private LocalDateTime applicationClose;

    @Embedded
    private EventCheckInDetails checkInDetails;


    @ManyToOne
    @JoinColumn(name = "application_form")
    private Form applicationForm;

    @Embedded
    private EventPaymentDetails paymentDetails;

    @Column(name = "options")
    @Builder.Default
    private List<EventOption> options = new ArrayList<>();

    @Column(name = "total_tickets_to_be_sold", nullable = false)
    private int totalTicketsToBeSold;

    @Column(name = "total_tickets_sold")
    private int totalTicketsSold;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id_fk", nullable = false)
    private Organization organization;

    @Column(nullable = false , length = 1000)
    private String description;

    public static Map<Privilege, AccessLevel> getOwnerPermission() {
        return Map.ofEntries(
                Map.entry(EventPrivilege.AUDIT, AccessLevel.READ),
                Map.entry(EventPrivilege.OPERATOR, AccessLevel.WRITE),
                Map.entry(EventPrivilege.CONFIG, AccessLevel.WRITE),
                Map.entry(EventPrivilege.APPLICATION, AccessLevel.WRITE)
        );
    }


    /******************************
     * display featues
     */
    //a full,css, javascript page for showcaase
    // and options to show useful metrics
    //put special tags
    //*********************************

    @PrePersist
    public void prePersist() {
        super.init(ResourceType.EVENT,this);
    }

    public boolean hasEnded()
    {
        if (end.isBefore(LocalDateTime.now())) {
            return true;
        }
        return false;
    }

    public boolean requiresApplication(){
        return options.contains(EventOption.REQUIRES_APPROVAL);
    }


    /**
     * Weather an event is currently accepting applications for tickets
     * depends on various factors like number of spots left, or if creator
     * of this event doesn't require an application, or is application period is
     * over. This assumes that event requires and application.
     * {@see requiresApplicaiton}
     * @return true if this event accepts application false otherwise
     */
    public boolean acceptsApplication(){
        if(
                 status.equals(EventStatus.EXPIRED)
                || status.equals(EventStatus.CANCELLED)
                || hasEnded()
        ) {
            return false;
        }
        return true;
    }
}
