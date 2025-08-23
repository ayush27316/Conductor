package com.conductor.core.model.org;

import com.conductor.core.model.common.BaseEntity;
import com.conductor.core.model.event.Event;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "organizations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Organization extends BaseEntity {

    @NotBlank(message = "Organization name is required")
    @Size(max = 255, message = "Name must be less than 255 characters")
    @Column(name = "name", nullable = false, length = 255, unique = true)
    @EqualsAndHashCode.Include
    private String name;

    @Column(name = "external_id")
    private int externalId;

    @NotBlank(message = "Organization name is required")
    @Size(max = 500, message = "Description must be less than 500 characters")
    @Column(name = "description", length = 500)
    private String description;

    @NotBlank(message = "Organization email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must be less than 255 characters")
    @Column(name = "email", unique = true, length = 255)
    private String email;

    /*organizations can put different tags to showcase it users */
    private List<String> tags;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column(name = "locations")
    private String locations;

    @OneToMany(mappedBy = "organization",
               orphanRemoval = true,
               fetch = FetchType.LAZY)
    private Set<Event> events = new HashSet<>();

}
