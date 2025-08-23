package com.conductor.core.model.audit;

import com.conductor.core.model.BaseEntity;
import com.conductor.core.model.Organization;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "organization_audits")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationAudit extends BaseEntity {

    @Column(name = "total_events_conducted")
    private int totalEventsConducted;
    @Column(name="total_tickets_sold")
    private int totalTicketsSold;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false, unique = true)
    @JsonIgnore
    private Organization organization;

    public static OrganizationAudit getBlankAudit(Organization organization){
        return OrganizationAudit.builder()
                .organization(organization)
                .totalEventsConducted(0)
                .totalTicketsSold(0).build();
    }

}
