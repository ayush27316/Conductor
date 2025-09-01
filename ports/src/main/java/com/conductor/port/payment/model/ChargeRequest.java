package com.conductor.port.payment.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargeRequest {

    private BigDecimal amount;
    private String currency;
    private String description;
    private String receiptEmail;

    /**
     * event processing happens asynchronously by the core. This means
     * when editing adapter must ensure that a reservationID is added
     * in it.
     *
     * This adds an extra complexity of maintaining a list of
     * vendor specific transaction id matched with reservationId.
     * Generally, all payment providers have a metadata field to
     * add extra information related to payment.
     */
    private String reservationExternalId;

    /**
     * Some providers require client's to generate payment source token to proceed with
     * payment. This token will be extracted from metadata of the client's http request
     * and added to this map along with other fields as per the requirement of payment
     * provider.
     */
    private Map<String,String> metadata;


}






