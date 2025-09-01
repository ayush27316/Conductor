package com.conductor.adapter.payment;

import com.conductor.port.payment.PaymentHandler;
import com.conductor.port.payment.exception.*;
import com.conductor.port.payment.model.*;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;

import jakarta.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class StripePaymentAdapter implements PaymentHandler {

    private final String webhookSecret;

    public StripePaymentAdapter(String apiKey, String webhookSecret) {
        Stripe.apiKey = apiKey;
        this.webhookSecret = webhookSecret;
    }

    @Override
    public ChargeResponse charge(ChargeRequest request)
            throws InvalidPaymentRequestException,
            PaymentProviderException,
            PayInRequestFailedException {
        try {
            if (request.getReservationExternalId() == null || request.getAmount() == null) {
                throw new InvalidPaymentRequestException("Missing required reservationId or amount");
            }

            Map<String, Object> params = new HashMap<>();
            params.put("amount", request.getAmount().multiply(BigDecimal.valueOf(100)).longValue()); // Stripe uses cents
            params.put("currency", request.getCurrency());
            params.put("description", request.getDescription());
            params.put("receipt_email", request.getReceiptEmail());

            // Attach metadata for internal correlation
            Map<String, String> metadata = new HashMap<>(Optional.ofNullable(request.getMetadata()).orElse(Map.of()));
            metadata.put("reservationExternalId", request.getReservationExternalId());
            params.put("metadata", metadata);

            // Create PaymentIntent
            PaymentIntent intent = PaymentIntent.create(params);

            Map<String, String> responseMetadata = new HashMap<>();
            responseMetadata.put("client_secret", intent.getClientSecret());

            return ChargeResponse.<String>builder()
                    .metadata(Optional.of(responseMetadata))
                    .build();

        } catch (StripeException e) {
            throw new PayInRequestFailedException("Stripe PaymentIntent creation failed");
        }
    }

    @Override
    public void registerWebhook(String url) throws WebhookRegistrationFailedException {
        // Stripe webhooks are usually registered manually via Dashboard or API.
        // You can implement this by calling Stripe’s API for webhook endpoints.
        // For now, we’ll throw unsupported.
        throw new WebhookRegistrationFailedException("Stripe webhook registration must be done via Dashboard/API");
    }

    @Override
    public PaymentEvent getPaymentEventFromWebhookRequest(HttpServletRequest request)
            throws WebhookAuthenticationException,
            PaymentProviderException {
        try {
            StringBuilder body = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }
            }

            String sigHeader = request.getHeader("Stripe-Signature");
            Event event = Webhook.constructEvent(body.toString(), sigHeader, webhookSecret);

            EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();

            if (event.getType().equals("payment_intent.succeeded")) {
                PaymentIntent pi = (PaymentIntent) deserializer.getObject().orElseThrow();
                return PaymentEvent.builder()
                        .eventCode(PaymentEvent.CHARGE_COMPLETED)
                        .reservationExternalId(pi.getMetadata().get("reservationExternalId"))
                        .timestamp(System.currentTimeMillis())
                        .build();
            } else if (event.getType().equals("payment_intent.payment_failed")) {
                PaymentIntent pi = (PaymentIntent) deserializer.getObject().orElseThrow();
                return PaymentEvent.builder()
                        .eventCode(PaymentEvent.CHARGE_FAILED)
                        .reservationExternalId(pi.getMetadata().get("reservationExternalId"))
                        .timestamp(System.currentTimeMillis())
                        .failureReason(pi.getLastPaymentError() != null
                                ? pi.getLastPaymentError().getMessage()
                                : "Unknown")
                        .build();
            } else if (event.getType().equals("payment_intent.processing")) {
                PaymentIntent pi = (PaymentIntent) deserializer.getObject().orElseThrow();
                return PaymentEvent.builder()
                        .eventCode(PaymentEvent.CHARGE_PENDING)
                        .reservationExternalId(pi.getMetadata().get("reservationExternalId"))
                        .timestamp(System.currentTimeMillis())
                        .build();
            } else {
                throw new PaymentProviderException("Unhandled Stripe event type: " + event.getType(), null);
            }

        } catch (IOException e) {
            throw new PaymentProviderException("Failed to read webhook request body", e);
        } catch (StripeException e) {
            throw new WebhookAuthenticationException("Invalid Stripe webhook signature");
        }
    }
}

