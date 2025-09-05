package com.conductor.core.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignupRequest {
    @NotBlank(message = "Username is required")
    @Size(max=50, message = "Username size exceeded")
    private String username;
    @NotBlank(message = "Password is required")
    @Size(max=100, message = "Password size exceeded")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email size must not exceed 100 characters")
    private String email;

    @JsonProperty("first_name")
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name size must not exceed 50 characters")
    private String firstName;

    @JsonProperty("last_name")
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name size must not exceed 50 characters")
    private String lastName;
}
