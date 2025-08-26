package com.conductor.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Standard response")
public class ResponseDTO<T> {

    @JsonProperty("api_name")
    private String apiName;
    private Integer status;
    private String responseTs;
    private Boolean success;
    private ErrorDetails errorDetails;
    private String message;
    @Schema(description = "generic payload for the response")
    private T payload;

    // 🔹 Timestamp generator
    private static String now() {
        return Instant.now().toString();
    }


    public static <T> ResponseDTO<T> success(String message) {
        return ResponseDTO.<T>builder()
                .status(HttpStatus.OK.value())
                .responseTs(now())
                .success(true)
                .message(message != null ? message : "Request processed successfully")
                .build();
    }



    public static <T> ResponseDTO<T> success(String apiName, T payload, String message) {
        return ResponseDTO.<T>builder()
                .apiName(apiName)
                .status(HttpStatus.OK.value())
                .responseTs(now())
                .success(true)
                .message(message != null ? message : "Request processed successfully")
                .payload(payload)
                .build();
    }

    public static <T> ResponseDTO<T> badRequest(String message) {
        return ResponseDTO.<T>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .responseTs(now())
                .success(false)
                .message(message != null ? message : "Bad request")
                .build();
    }

    // ✅ Bad Request (400)
    public static <T> ResponseDTO<T> badRequest(String apiName, String message, ErrorDetails errorDetails) {
        return ResponseDTO.<T>builder()
                .apiName(apiName)
                .status(HttpStatus.BAD_REQUEST.value())
                .responseTs(now())
                .success(false)
                .message(message != null ? message : "Bad request")
                .errorDetails(errorDetails)
                .build();
    }

    // ✅ Unauthorized (401)
    public static <T> ResponseDTO<T> unauthorized(String apiName, String message) {
        return ResponseDTO.<T>builder()
                .apiName(apiName)
                .status(HttpStatus.UNAUTHORIZED.value())
                .responseTs(now())
                .success(false)
                .message(message != null ? message : "Unauthorized")
                .build();
    }

    // ✅ Internal Server Error (500)
    public static <T> ResponseDTO<T> error(String apiName, String message, ErrorDetails errorDetails) {
        return ResponseDTO.<T>builder()
                .apiName(apiName)
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .responseTs(now())
                .success(false)
                .message(message != null ? message : "An unexpected error occurred")
                .errorDetails(errorDetails)
                .build();
    }
}
