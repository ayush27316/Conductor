package com.conductor.port.payment.exception;

public class InvalidPaymentRequestException extends Exception {
    public InvalidPaymentRequestException(String message) {
        super(message);
    }
}

