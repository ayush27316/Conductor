package com.conductor.core.model.event;

import com.conductor.core.model.common.File;
import com.conductor.core.model.common.Resource;
import com.conductor.core.model.common.ResourceType;
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

    @Column(name = "external_id", nullable = false, updatable = false, unique = true)
    @Builder.Default
    private String externalId = UUID.randomUUID().toString();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventFormat format;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name="website_url")
    private String websiteUrl;

    @Column
    private String location;

    @Column(name = "begin_time", nullable = false)
    private LocalDateTime begin;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime end;

    @Embedded
    private EventAccessDetails accessDetails;

    @Column(name = "options")
    @Builder.Default
    private List<EventOption> options = new ArrayList<>();

    @Column(name = "total_tickets_to_be_sold", nullable = false)
    private int totalTicketsToBeSold;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id_fk", nullable = false)
    private Organization organization;

//    @OneToOne
//    @JoinColumn(name = "image_file_id_fk")
//    private File imageFile;

    @Column(nullable = false , length = 1000)
    private String description;


    @PrePersist
    public void prePersist() {
        super.setResourceType(ResourceType.EVENT);
        if (externalId == null) {
            externalId = UUID.randomUUID().toString();
        }
    }
}
