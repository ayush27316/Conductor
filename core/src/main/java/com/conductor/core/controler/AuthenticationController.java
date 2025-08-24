package com.conductor.core.controler;

import com.conductor.core.dto.auth.LoginRequestDTO;
import com.conductor.core.dto.auth.LoginResponseDTO;
import com.conductor.core.dto.auth.SignUpRequestDTO;
import com.conductor.core.dto.auth.SignUpResponseDTO;
import com.conductor.core.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDto) {
        return ResponseEntity.ok().body(authService.login(loginRequestDto));
    }

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDTO> signup(@RequestBody SignUpRequestDTO signupRequestDto) {
        return ResponseEntity.ok(authService.signup(signupRequestDto));
    }
}