package com.conductor.core.model.user;

import com.conductor.core.model.Resource;
import com.conductor.core.model.ResourceType;
import com.conductor.core.model.org.Organization;
import jakarta.persistence.*;
import lombok.*;

/**
 * {@link Organization}'s can create  users aka operators to facilitate
 * different activities within an event for example; check-in. Operator's
 * created by an organization can grant permissions to resources owned by
 * it.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "operators")
public class Operator extends Resource {

    @OneToOne(optional = false, fetch =FetchType.EAGER)
    @JoinColumn(name = "user_id_fk")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "organization_id_fk")
    private Organization organization;


    @PrePersist
    public void prePersist() {
        super.init(ResourceType.OPERATOR, this);
    }
}
