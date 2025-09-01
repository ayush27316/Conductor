package com.conductor.core.model.event;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public class EventPaymentDetails {

    private BigDecimal price;
    private String currency;
}
