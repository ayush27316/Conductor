package com.conductor.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrganizationApplicationRequest {
    @NotBlank(message = "Organization name is required.")
    @Size(max = 100, message = "Website URL must be at most 200 characters.")
    private String name;

    @Size(max = 500, message = "Description must be at most 500 characters.")
    private String description;

    @NotBlank(message = "Organization email is required.")
    @Email(message = "Invalid email format.")
    @Size(max = 200, message = "Website URL must be at most 200 characters.")
    private String email;

    @Schema(description =
            """
                Tags related to the organization. If the organization is
                approved these tags are shows on organizations public profile.
                Can be added later as well.
            """,
            example = "[\"AI\",\"Orchestration\",\"Cloud\"]")
    private List<String> tags;

    @JsonProperty("website_url")
    @Size(max = 200, message = "Website URL must be at most 200 characters.")
    private String websiteUrl;

    @NotBlank(message = "Organization location is required")
    @Size(max = 100, message = "Website URL must be at most 200 characters.")
    private String locations;

}
