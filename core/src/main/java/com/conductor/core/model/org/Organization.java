package com.conductor.core.model.org;

import com.conductor.core.model.common.Resource;
import com.conductor.core.model.common.ResourceType;
import com.conductor.core.model.event.Event;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "organizations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization extends Resource {

    @Column(name = "external_id", nullable = false, updatable = false, unique = true)
    @Builder.Default
    private String externalId = UUID.randomUUID().toString();

    @Column(name = "name", nullable = false, length = 255, unique = true)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    @Column
    private List<String> tags;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column
    private String locations;

    @OneToMany(mappedBy = "organization",
               orphanRemoval = true,
               fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Event> events = new HashSet<>();

    @PrePersist
    public void prePersist() {
        super.setResourceType(ResourceType.ORGANIZATION);
        if (externalId == null) {
            externalId = UUID.randomUUID().toString();
        }
    }
}

