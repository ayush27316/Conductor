package com.conductor.core.service;

import com.conductor.core.dto.auth.LoginRequestDTO;
import com.conductor.core.dto.auth.LoginResponseDTO;
import com.conductor.core.dto.auth.SignUpRequestDTO;
import com.conductor.core.dto.auth.SignUpResponseDTO;
import com.conductor.core.model.user.User;
import com.conductor.core.model.user.UserType;
import com.conductor.core.repository.UserRepository;
import com.conductor.core.security.JwtUtil;
import com.conductor.core.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil authUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponseDTO login(LoginRequestDTO loginRequestDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword())
            );

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = userRepository.findById(userPrincipal.getId())
                    .orElseThrow(() -> new BadCredentialsException("User not found"));

            String token = authUtil.generateAccessToken(user);
            return new LoginResponseDTO(token);
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    public SignUpResponseDTO signup(SignUpRequestDTO signupRequestDto) {
        User user = userRepository.findByUsername(signupRequestDto.getUsername()).orElse(null);

        if(user != null) throw new IllegalArgumentException("User already exists");

        user = User.builder()
                .username(signupRequestDto.getUsername())
                .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                .firstName(signupRequestDto.getFirstName())
                .lastName(signupRequestDto.getLastName())
                .emailAddress(signupRequestDto.getEmail())
                .type(signupRequestDto.getUserType())
                .build();


        user = userRepository.save(user);

        return new SignUpResponseDTO(user.getId(), user.getUsername());
    }
}














