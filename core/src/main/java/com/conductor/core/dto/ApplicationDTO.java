package com.conductor.core.dto;

import com.conductor.core.model.application.ApplicationStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationDTO {

    @JsonProperty("organization_id")
    private String organizationExternalId;

    @JsonProperty("event_id")
    private String eventExternalId;

    @JsonProperty("submitted_by_user_id")
    private String submittedByUserExternalId;

    @JsonProperty("submitted_at")
    private LocalDateTime submittedAt = LocalDateTime.now();

    @JsonProperty("application_status")
    private ApplicationStatus applicationStatus;

    @JsonProperty("processed_by_user_id")
    private String processedByUserExternalId;

    @JsonProperty("processed_at")
    private LocalDateTime processedAt;

    @JsonProperty("comments")
    private List<Comment> comments;

    @JsonProperty("application_form")
    private String applicationForm;

    @JsonProperty("application_form_response")
    private String applicationFormResponse;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Comment {

        @JsonProperty("author_id")
        private String authorExternalId;

        @JsonProperty("content")
        private String content;

        @JsonProperty("created_at")
        private LocalDateTime createdAt;
    }
}
