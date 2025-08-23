package com.conductor.core.model.ticket;

/*
* Ticket's creation strategy can be compounded together
* with certain restrictions to obtain custom behaviour
* */
public enum TicketCreationStrategy {

    /* distribution strategies */
    DISTRIBUTION_PRE_EVENT,
    DISTRIBUTION_THROUGHOUT_EVENT,
    CUSTOM_DISTRIBUTION,

    /* */
    REQUIRES_APPROVAL,

    /*payment related strategies*/
    REQUIRES_PAYMENT,
    /* requires payment to book a ticket (access to event may still be
    * allowed only after approval) */
    REQUIRES_PAYMENT_PRE_BOOKING,
    REQUIRES_PAYMENT_POST_APPROVAL,

    QUANTITY_LIMITED,
    QUANTITY_UNLIMITED
}
