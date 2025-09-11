package com.conductor.core.exception;

import com.conductor.core.dto.Error;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle EventNotFoundException - when an event cannot be found.
     */
    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<Error> handleEventNotFound(
            EventNotFoundException ex,
            WebRequest request) {

        log.warn("Event not found: {}", ex.getMessage());

        Error error = Error.builder()
                .error("Not Found")
                .message("The requested event could not be found")
                .timestamp(Instant.now().toString())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handle ApplicationNotFoundException - when an application cannot be found.
     */
    @ExceptionHandler(ApplicationNotFound.class)
    public ResponseEntity<Error> handleApplicationNotFound(
            ApplicationNotFound ex,
            WebRequest request) {

        log.warn("Application not found: {}", ex.getMessage());

        Error error = Error.builder()
                .error("Not Found")
                .message("The requested application could not be found")
                .timestamp(Instant.now().toString())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handle FormNotFoundException - when an application form cannot be found.
     */
    @ExceptionHandler(FormNotFoundException.class)
    public ResponseEntity<Error> handleFormNotFound(
            FormNotFoundException ex,
            WebRequest request) {

        log.warn("Form not found: {}", ex.getMessage());

        Error error = Error.builder()
                .error("Not Found")
                .message("The application does not have an associated form")
                .timestamp(Instant.now().toString())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handle ApplicationRequestFailedException - business logic violations.
     * This maps to different HTTP status codes based on the specific scenario.
     */
    @ExceptionHandler(ApplicationRequestFailedException.class)
    public ResponseEntity<Error> handleApplicationRequestFailed(
            ApplicationRequestFailedException ex,
            WebRequest request) {

        log.warn("Application request failed: {}", ex.getMessage());

        String message = ex.getMessage();
        HttpStatus status;

        // Determine appropriate HTTP status based on error message
        if (message != null) {
            if (message.contains("already exists") ||
                    message.contains("no longer accepting")) {
                status = HttpStatus.CONFLICT;
            } else if (message.contains("does not accept") ||
                    message.contains("invalid") ||
                    message.contains("form response")) {
                status = HttpStatus.BAD_REQUEST;
            } else {
                // Default to internal server error for database operation failures
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        Error error = Error.builder()
                .error(getErrorTypeFromStatus(status))
                .message(message != null ? message : "Application request failed")
                .timestamp(Instant.now().toString())
                .build();

        return ResponseEntity.status(status).body(error);
    }

    /**
     * Handle IllegalArgumentException - invalid arguments passed to service methods.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Error> handleIllegalArgument(
            IllegalArgumentException ex,
            WebRequest request) {

        log.warn("Invalid argument: {}", ex.getMessage());

        Error error = Error.builder()
                .error("Bad Request")
                .message("Invalid request parameter: " + ex.getMessage())
                .timestamp(Instant.now().toString())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle validation errors from @Valid annotations.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> handleValidationErrors(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        log.warn("Validation error: {}", ex.getMessage());

        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                validationErrors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> details = new HashMap<>();
        details.put("validation_errors", validationErrors);

        Error error = Error.builder()
                .error("Bad Request")
                .message("Validation failed")
                .timestamp(Instant.now().toString())
                .details(details)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle constraint violations from @Validated annotations.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Error> handleConstraintViolation(
            ConstraintViolationException ex,
            WebRequest request) {

        log.warn("Constraint violation: {}", ex.getMessage());

        String violations = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        Error error = Error.builder()
                .error("Bad Request")
                .message("Constraint violation: " + violations)
                .timestamp(Instant.now().toString())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle any other unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleGenericException(
            Exception ex,
            WebRequest request) {

        log.error("Unexpected error occurred", ex);

        Error error = Error.builder()
                .error("Internal Server Error")
                .message("An unexpected error occurred. Please try again later.")
                .timestamp(Instant.now().toString())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Convert HTTP status to error type string.
     */
    private String getErrorTypeFromStatus(HttpStatus status) {
        switch (status) {
            case BAD_REQUEST:
                return "Bad Request";
            case NOT_FOUND:
                return "Not Found";
            case CONFLICT:
                return "Conflict";
            case INTERNAL_SERVER_ERROR:
                return "Internal Server Error";
            case FORBIDDEN:
                return "Forbidden";
            case UNAUTHORIZED:
                return "Unauthorized";
            default:
                return "Error";
        }
    }

    @ExceptionHandler(InvalidServiceRequest.class)
    public ResponseEntity<Error> handleInvalidServiceRequest(
            InvalidServiceRequest ex, WebRequest request) {

         Error error = Error.builder()
                .error(HttpStatus.BAD_REQUEST.toString())
                .success(false)
                .message("Invalid service request: " + ex.getMessage())
                .timestamp(LocalDateTime.now().toString())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Error> handleInvalidServiceRequest(
            AccessDeniedException ex, WebRequest request) {

        Error error = Error.builder()
                .error(HttpStatus.UNAUTHORIZED.toString())
                .success(false)
                .message("Invalid service request: " + ex.getMessage())
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);

    }

    @ExceptionHandler({SecurityException.class, TokenNotValidException.class})
    public ResponseEntity<Error> handleUnauthorizedExceptions(
            Exception ex, WebRequest request) {

        String message = ex instanceof TokenNotValidException
                ? "Token not valid: " + ex.getMessage()
                : "Unauthorized access: " + ex.getMessage();

        Error error = Error.builder()
                .error(HttpStatus.UNAUTHORIZED.toString())
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);

    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Error> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException ife) {
            Class<?> targetType = ife.getTargetType();

            if (targetType.isEnum()) {
                Object[] enumConstants = targetType.getEnumConstants();

                Map<String, Object> details = new HashMap<>();
                details.put("invalidValue", ife.getValue());
                details.put("acceptedValues", Arrays.stream(enumConstants)
                        .map(e -> e.toString().toLowerCase())
                        .toList());

                Error errorResponse = Error.builder()
                        .error("Bad Request")
                        .success(false)
                        .message("Invalid value for enum " + targetType.getSimpleName())
                        .timestamp(Instant.now().toString())
                        .details(details)
                        .build();

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
        }

        // fallback error
        Error fallbackError = Error.builder()
                .error("Bad Request")
                .success(false)
                .message("Malformed JSON request")
                .timestamp(Instant.now().toString())
                .details(Map.of("cause", ex.getMostSpecificCause().getMessage()))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(fallbackError);
    }


    @ExceptionHandler(OrganizationNotFound.class)
    public ResponseEntity<Error> handleOrganizationNotFound(
            OrganizationNotFound ex, WebRequest request) {

        Error error = Error.builder()
                .error(HttpStatus.NOT_FOUND.toString())
                .success(false)
                .message("Organization not found")
                .timestamp(LocalDateTime.now().toString())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Error> handleNoSuchElement(
            NoSuchElementException ex, WebRequest request) {

        Error error = Error.builder()
                .error(HttpStatus.BAD_REQUEST.toString())
                .success(false)
                .message("Invalid request data")
                .timestamp(LocalDateTime.now().toString())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

    }


    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Error> handleNoSuchElement(
            AuthorizationDeniedException ex, WebRequest request) {

        Error error = Error.builder()
                .error(HttpStatus.UNAUTHORIZED.toString())
                .success(false)
                .message("Access Denied")
                .timestamp(LocalDateTime.now().toString())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);

    }
    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
//
//    @ExceptionHandler(InvalidFormatException.class)
//    public ResponseEntity<String> handleInvalidFormat(InvalidFormatException ex) {
//        Class<?> targetType = ex.getTargetType();
//
//        if (Option.class.isAssignableFrom(targetType)) {
//
//            @SuppressWarnings("unchecked")
//            String allowedValues = Option.getAllOptions(targetType.getClass());
//
//            String message = String.format(
//                    "Invalid value '%s' for %s. Allowed values are: %s",
//                    ex.getValue(), targetType.getSimpleName(), allowedValues
//            );
//            return ResponseEntity.badRequest().body(message);
//        }
//
//        // fallback for other types
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getOriginalMessage());
//    }

//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Error> handleValidationExceptions(
//            MethodArgumentNotValidException ex, WebRequest request) {
//
//        List<String> errors = ex.getBindingResult()
//                .getFieldErrors()
//                .stream()
//                .map(error -> error.getField() + ": " + error.getDefaultMessage())
//                .collect(Collectors.toList());
//
//        Error error = Error.builder()
//                .error(HttpStatus.BAD_REQUEST.toString())
//                .success(false)
//                .message("Validation failed")
//                .timestamp(LocalDateTime.now().toString())
//                .build();
//
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
//
//    }
