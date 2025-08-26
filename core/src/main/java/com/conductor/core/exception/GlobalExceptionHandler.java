package com.conductor.core.exception;

import com.conductor.core.dto.ErrorDetails;
import com.conductor.core.dto.ResponseDTO;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidServiceRequest.class)
    public ResponseEntity<ResponseDTO<?>> handleInvalidServiceRequest(
            InvalidServiceRequest ex, WebRequest request) {

        ErrorDetails errorDetails = ErrorDetails.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .description(ex.getMessage())
                .build();

        ResponseDTO<?> response = ResponseDTO.badRequest(
                extractPath(request),
                "Invalid service request",
                errorDetails
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ResponseDTO<?>> handleSecurityException(
            SecurityException ex, WebRequest request) {

        ErrorDetails errorDetails = ErrorDetails.builder()
                .code(HttpStatus.UNAUTHORIZED.value())
                .description(ex.getMessage())
                .build();

        ResponseDTO<?> response = ResponseDTO.unauthorized(
                extractPath(request),
                "Unauthorized access"
        );
        response.setErrorDetails(errorDetails);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // Handle all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO<?>> handleGlobalException(
            Exception ex, WebRequest request) {

        ErrorDetails errorDetails = ErrorDetails.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .description(ex.getMessage())
                .build();

        ResponseDTO<?> response = ResponseDTO.error(
                extractPath(request),
                "An unexpected error occurred. Please try again later.",
                errorDetails
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO<?>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ErrorDetails errorDetails = ErrorDetails.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .description(String.join(", ", errors))
                .build();

        ResponseDTO<?> response = ResponseDTO.badRequest(
                extractPath(request),
                "Validation failed",
                errorDetails
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseDTO<?>> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(fieldName, message);
        });

        ErrorDetails errorDetails = ErrorDetails.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .description(errors.toString())
                .build();

        ResponseDTO<?> response = ResponseDTO.badRequest(
                extractPath(request),
                "Constraint violation",
                errorDetails
        );

        return ResponseEntity.badRequest().body(response);
    }

    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
