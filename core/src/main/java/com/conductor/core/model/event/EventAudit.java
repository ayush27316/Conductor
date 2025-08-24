package com.conductor.core.model.event;

import com.conductor.core.model.permission.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="event_audit")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventAudit extends BaseEntity {

    @Column(name = "total_users_enrolled")
    private int totalUsersEnrolled;
    @Column(name ="total_tickets_sold")
    private int totalTicketsSold;

    @OneToOne(optional = false)
    @JoinColumn(name = "event_id_fk", nullable=false)
    private Event event;
}
