package com.conductor.core.dto;

import com.conductor.core.model.application.ApplicationStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // exclude null fields globally
public class Application {

    @NotBlank(message = "Organization External Id is required.")
    @Size(min = 36, max = 36, message = "Organization External Id must be a valid UUID (36 characters).")
    @JsonProperty("organization_id")
    private String organizationExternalId;

    @NotBlank(message = "Event External Id is required.")
    @Size(min = 36, max = 36, message = "Event External Id must be a valid UUID (36 characters).")
    @JsonProperty("event_id")
    private String eventExternalId;

    @NotBlank(message = "Submitted By User External Id is required.")
    @Size(min = 36, max = 36, message = "Submitted By User External Id must be a valid UUID (36 characters).")
    @JsonProperty("submitted_by_user_id")
    private String submittedByUserExternalId;

    @NotNull(message = "Submitted at cannot be null.")
    @JsonProperty("submitted_at")
    private LocalDateTime submittedAt = LocalDateTime.now();

    @NotNull(message = "Application status is required.")
    @JsonProperty("application_status")
    private ApplicationStatus applicationStatus;

    @Size(min = 36, max = 36, message = "Processed By User External Id must be a valid UUID (36 characters).")
    @JsonProperty("processed_by_user_id")
    private String processedByUserExternalId;

    @JsonProperty("processed_at")
    private LocalDateTime processedAt;

    @JsonProperty("comments")
    private List<Comment> comments;

    @NotBlank(message = "Application form is required.")
    @Size(max = 5000, message = "Application form must not exceed 5000 characters.")
    @JsonProperty("application_form")
    private String applicationForm;

    @Size(max = 5000, message = "Application form response must not exceed 5000 characters.")
    @JsonProperty("application_form_response")
    private String applicationFormResponse;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Comment {

        @NotBlank(message = "Author External Id is required.")
        @Size(min = 36, max = 36, message = "Author External Id must be a valid UUID (36 characters).")
        @JsonProperty("author_id")
        private String authorExternalId;

        @NotBlank(message = "Comment content is required.")
        @Size(max = 1000, message = "Comment content must not exceed 1000 characters.")
        @JsonProperty("content")
        private String content;

        @NotNull(message = "Created At cannot be null.")
        @JsonProperty("created_at")
        private LocalDateTime createdAt;
    }
}
