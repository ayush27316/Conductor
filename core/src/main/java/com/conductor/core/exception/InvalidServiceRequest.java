package com.conductor.core.exception;

public class InvalidServiceRequest extends RuntimeException {
    public InvalidServiceRequest(String message) {
        super(message);
    }
}
