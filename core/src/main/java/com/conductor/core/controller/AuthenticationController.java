package com.conductor.core.controller;

import com.conductor.core.dto.ErrorDetails;
import com.conductor.core.dto.ResponseDTO;
import com.conductor.core.dto.auth.LoginRequestDTO;
import com.conductor.core.dto.auth.LoginResponseDTO;
import com.conductor.core.dto.auth.SignUpRequestDTO;
import com.conductor.core.dto.auth.SignUpResponseDTO;
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
    public ResponseDTO<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            LoginResponseDTO response = authenticationService.login(loginRequest);
            return ResponseDTO.success("/login", response, "Login Successful.");
        } catch (BadCredentialsException e) {
            return  ResponseDTO.unauthorized("/login","Username or password invalid");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getStackTrace());
            return ResponseDTO.error("/login","An internal error occurred. Please try again.",ErrorDetails.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).build());
        }
    }

    @PostMapping("/signup")
    public ResponseDTO<?> signup(@Valid @RequestBody SignUpRequestDTO signupRequest) {
        try {
            authenticationService.signup(signupRequest);
            return ResponseDTO.success("/signup", null, "Signup successfully. Proceed with login.");

        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof UsernameAlreadyTakenException){

               return ResponseDTO.builder().errorDetails(ErrorDetails.builder().code(HttpStatus.CONFLICT.value()).build())
                        .build();
            }

            return ResponseDTO.builder().errorDetails(ErrorDetails.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).build())
                    .build();

        }
    }
}