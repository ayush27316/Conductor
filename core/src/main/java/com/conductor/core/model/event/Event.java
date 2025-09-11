package com.conductor.core.model.event;

import com.conductor.core.model.Resource;
import com.conductor.core.model.ResourceType;
import com.conductor.core.model.form.Form;
import com.conductor.core.model.org.Organization;
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

    /**
     * If {@code EventOption.REQUIRE_APPROVAL} is selected then form
     * is required.
     */
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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id_fk", nullable = false)
    private Organization organization;

    @Column(nullable = false , length = 1000)
    private String description;


    /******************************
     * display featues
     */
    //a full,css, javascript page for showcaase
    // and options to show useful metrics
    //put special tags
    //*********************************

    @PrePersist
    public void prePersist() {
        super.init(ResourceType.EVENT,null);
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
