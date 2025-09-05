package com.conductor.core.model.event;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.math.BigDecimal;

@Embeddable
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventPaymentDetails {
    private boolean isfree;
    private BigDecimal ticketPrice;
    private String currency;
}
