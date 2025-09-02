package com.conductor.core.exception;

import com.conductor.core.dto.ResponseDTO;
import com.conductor.core.model.common.Option;
import com.conductor.core.model.event.EventOption;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidServiceRequest.class)
    public ResponseDTO<?> handleInvalidServiceRequest(
            InvalidServiceRequest ex, WebRequest request) {

        return ResponseDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .success(false)
                .message("Invalid service request: " + ex.getMessage())
                .description("Path: " + extractPath(request))
                .timeStamp(LocalDateTime.now().toString())
                .build();
    }


    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseDTO<?> handleInvalidServiceRequest(
            AccessDeniedException ex, WebRequest request) {

        return ResponseDTO.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .success(false)
                .message("Invalid service request: " + ex.getMessage())
                .description("Path: " + extractPath(request))
                .timeStamp(LocalDateTime.now().toString())
                .build();
    }


    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({SecurityException.class, TokenNotValidException.class})
    public ResponseDTO<?> handleUnauthorizedExceptions(
            Exception ex, WebRequest request) {

        String message = ex instanceof TokenNotValidException
                ? "Token not valid: " + ex.getMessage()
                : "Unauthorized access: " + ex.getMessage();

        return ResponseDTO.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .success(false)
                .message(message)
                .description("Path: " + extractPath(request))
                .timeStamp(LocalDateTime.now().toString())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseDTO<?> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        return ResponseDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .success(false)
                .message("Validation failed")
                .description(String.join(", ", errors) + " | Path: " + extractPath(request))
                .timeStamp(LocalDateTime.now().toString())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseDTO<?> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {

        String errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));

        return ResponseDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .success(false)
                .message("Constraint violation")
                .description(errors + " | Path: " + extractPath(request))
                .timeStamp(LocalDateTime.now().toString())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseDTO<?> handleNoSuchElement(
            NoSuchElementException ex, WebRequest request) {

        return ResponseDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .success(false)
                .message("Invalid request data")
                .description("The requested value was not found: " + ex.getMessage() + " | Path: " + extractPath(request))
                .timeStamp(LocalDateTime.now().toString())
                .build();
    }

    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
