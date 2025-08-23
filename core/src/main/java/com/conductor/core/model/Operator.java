package com.conductor.core.model;

import com.conductor.core.model.user.Role;
import com.conductor.core.model.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import com.conductor.core.model.BaseEntity;
import com.conductor.core.model.Organization;
import com.conductor.core.model.ticket.Ticket;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**/
@Entity
@Table(name = "operators")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Operator  extends  BaseEntity{

    /* user of type operator*/
    @OneToOne(cascade = CascadeType.ALL)
    private User user;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "organization_id_fk")
    private Organization organization;

    @ManyToMany
    @JsonBackReference
    @JsonIgnore
    private Set<Event> events = new HashSet<>();

}
