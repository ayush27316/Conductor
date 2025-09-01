package com.conductor.port.payment.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {

    public static final int CHARGE_COMPLETED = 0;
    public static final int CHARGE_PENDING = 1;
    public static final int CHARGE_FAILED = 2;

    private int eventCode;
    private String reservationExternalId;
    private long timestamp; // Added for event timestamp
    private String failureReason; // Added for failure details

    public boolean isCompleted() {
        return eventCode == CHARGE_COMPLETED;
    }

    public boolean isPending() {
        return eventCode == CHARGE_PENDING;
    }

    public boolean isFailed() {
        return eventCode == CHARGE_FAILED;
    }
}
