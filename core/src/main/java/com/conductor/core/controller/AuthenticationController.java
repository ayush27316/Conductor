package com.conductor.core.controller;

import com.conductor.core.dto.auth.LoginRequestDTO;
import com.conductor.core.dto.auth.LoginResponseDTO;
import com.conductor.core.dto.auth.SignUpRequestDTO;
import com.conductor.core.exception.UsernameAlreadyTakenException;
import com.conductor.core.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            LoginResponseDTO response = authenticationService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username or password invalid");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An internal server error occurred");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignUpRequestDTO signupRequest) {
        try {
            authenticationService.signup(signupRequest);
            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch(UsernameAlreadyTakenException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken.");
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An internal server error occurred");

        }
    }
}