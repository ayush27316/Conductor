package com.conductor.port.payment.model;

import lombok.*;


import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargeResponse<T> {

    /**
     * An optional map of key and values that will be added in the http response
     * that is sent to the client. Some payment providers requires to share
     * a toke to clients which can then proceed with actual payment using the token.
     * This map will be useful in providing that token to user.
     *
     * eg: {
     *     "client_secret" : "verinaoier34t85u038t934",
     *     "redirect_url" : "http://stripe.pay.checkout"
     * }
     */
    private Optional<Map<String,String>> metadata;
}


