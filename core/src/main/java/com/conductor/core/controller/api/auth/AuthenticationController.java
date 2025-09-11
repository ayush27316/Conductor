package com.conductor.core.controller.api.auth;

import com.conductor.core.dto.Error;
import com.conductor.core.dto.auth.LoginRequest;
import com.conductor.core.dto.auth.SignupRequest;
import com.conductor.core.exception.UsernameAlreadyTakenException;
import com.conductor.core.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest loginRequest) {
        try {

            String jwt = authenticationService.login(loginRequest);
            return ResponseEntity.ok().body(Map.of("jwt",jwt));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Error.builder()
                            .error(HttpStatus.UNAUTHORIZED.toString())
                            .success(false)
                            .message("Username or password invalid")
                            .timestamp(LocalDateTime.now().toString()).build()
                    );
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            authenticationService.signup(signupRequest);
            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch(UsernameAlreadyTakenException e){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Error.builder()
                            .error(HttpStatus.CONFLICT.toString())
                            .success(false)
                            .message("Username already taken")
                            .timestamp(LocalDateTime.now().toString()).build());
        }
    }
}