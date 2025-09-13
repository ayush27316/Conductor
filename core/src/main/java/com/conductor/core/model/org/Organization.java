package com.conductor.core.model.org;

import com.conductor.core.model.Resource;
import com.conductor.core.model.ResourceType;
import com.conductor.core.model.event.Event;
import com.conductor.core.model.permission.AccessLevel;
import com.conductor.core.model.permission.Privilege;
import com.conductor.core.model.user.Operator;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "organizations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization extends Resource {

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

    @OneToMany(mappedBy = "organization",
               cascade = CascadeType.PERSIST,
               fetch = FetchType.LAZY,
               orphanRemoval = true)
    private Set<Operator> operators = new HashSet<>();

    @PrePersist
    public void prePersist() {
        super.init(ResourceType.ORGANIZATION, this);
    }


    public static Map<Privilege, com.conductor.core.model.permission.AccessLevel> getOwnerPermission() {
        return Map.ofEntries(
                Map.entry(OrganizationPrivilege.EVENT, com.conductor.core.model.permission.AccessLevel.WRITE),
                Map.entry(OrganizationPrivilege.OPERATOR, com.conductor.core.model.permission.AccessLevel.WRITE),
                Map.entry(OrganizationPrivilege.CONFIG, com.conductor.core.model.permission.AccessLevel.WRITE),
                Map.entry(OrganizationPrivilege.AUDIT, AccessLevel.READ)

        );
    }

}

