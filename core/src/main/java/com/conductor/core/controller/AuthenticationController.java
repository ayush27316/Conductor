package com.conductor.core.controller;

import com.conductor.core.dto.ResponseDTO;
import com.conductor.core.dto.auth.LoginRequestDTO;
import com.conductor.core.dto.auth.LoginResponseDTO;
import com.conductor.core.dto.auth.SignUpRequestDTO;
import com.conductor.core.exception.UsernameAlreadyTakenException;
import com.conductor.core.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseDTO<?> login(
            @Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            LoginResponseDTO response = authenticationService.login(loginRequest);
            return ResponseDTO.success("Login Successful.", response);
        } catch (BadCredentialsException e) {
            return  ResponseDTO.unauthorized("Username or password invalid");
        } catch (Exception e) {
            return ResponseDTO.internalServerError("An internal server error occurred");
        }
    }

    @PostMapping("/signup")
    public ResponseDTO<?> signup(@Valid @RequestBody SignUpRequestDTO signupRequest) {
        try {
            authenticationService.signup(signupRequest);
            return ResponseDTO.success("Signup successfully. Proceed with login.");

        } catch(UsernameAlreadyTakenException e){
            return ResponseDTO.builder()
                    .status(HttpStatus.CONFLICT.value())
                    .success(false)
                    .message("Username already taken.")
                    .build();
        }
        catch (Exception e) {
            return ResponseDTO.internalServerError("An internal server error occurred");

        }
    }
}