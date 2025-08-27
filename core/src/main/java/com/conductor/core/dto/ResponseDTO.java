package com.conductor.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;


import java.time.LocalDateTime;

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

    private Integer status;
    private Boolean success;
    private String message;
    @JsonProperty("time_stamp")
    private String timeStamp;
    @JsonProperty("error_description")
    private String description;

    @Schema(description = "generic payload for the response")
    private T payload;




    public static <T> ResponseDTO<T> success(String message) {
        return ResponseDTO.<T>builder()
                .status(HttpStatus.OK.value())
                .timeStamp(now())
                .success(true)
                .message(message != null ? message : "Request processed successfully")
                .build();
    }



    public static <T> ResponseDTO<T> success(String message, T payload) {
        return ResponseDTO.<T>builder()
                .status(HttpStatus.OK.value())
                .timeStamp(now())
                .success(true)
                .message(message != null ? message : "Request processed successfully")
                .payload(payload)
                .build();
    }

    public static <T> ResponseDTO<T> badRequest(String message) {
        return ResponseDTO.<T>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .timeStamp(now())
                .success(false)
                .message(message != null ? message : "Bad request")
                .build();
    }

    public static <T> ResponseDTO<T> unauthorized(String message) {
        return ResponseDTO.<T>builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .timeStamp(now())
                .success(false)
                .message(message != null ? message : "Unauthorized")
                .build();
    }


    public static <T> ResponseDTO<T> internalServerError(String message) {
        return ResponseDTO.<T>builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timeStamp(now())
                .success(false)
                .message(message != null ? message : "Internal Server Error")
                .build();
    }

    private static String now() {
        return LocalDateTime.now().toString();
    }

}
