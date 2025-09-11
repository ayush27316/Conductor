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

    @Schema(description =
            """ 
                Organization's contact email. This email is used for all
                communication with the organization. After organization
                is approved credentials of a user with owner privilege is 
                sent on this email
            """,
            example = "contact@conductor.io")
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

    @Schema(description =
            """
                A website, social media profile link of the organization,
                that might be useful in identifying the organization during
                the approval process.
            """,
                example = "https://www.conductor.io")
    @JsonProperty("website_url")
    @Size(max = 200, message = "Website URL must be at most 200 characters.")
    private String websiteUrl;

    @NotBlank(message = "Organization location is required")
    @Size(max = 100, message = "Website URL must be at most 200 characters.")
    private String locations;

}
