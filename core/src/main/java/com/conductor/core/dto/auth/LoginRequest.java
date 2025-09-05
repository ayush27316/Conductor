package com.conductor.core.dto.auth;

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
public class LoginRequestDTO {
    @NotBlank(message = "Username is required")
    @Size(max=50, message = "Username size exceeded")
    private String username;
    @NotBlank(message = "Password is required")
    @Size(max=100, message = "Password size exceeded")
    private String password;
}