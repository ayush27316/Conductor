package com.conductor.core.service;

import com.conductor.core.dto.auth.LoginRequest;
import com.conductor.core.dto.auth.SignupRequest;
import com.conductor.core.exception.UsernameAlreadyTakenException;
import com.conductor.core.model.user.User;
import com.conductor.core.model.user.UserRole;
import com.conductor.core.repository.UserRepository;
import com.conductor.core.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
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

    public String login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        User user =  (User) authentication.getPrincipal();

        String token = authUtil.generateAccessToken(user);
        return token;
    }


    public void signup(SignupRequest signupRequest) {

        if(!userRepository.findByUsername(signupRequest.getUsername()).isEmpty()){
            throw new UsernameAlreadyTakenException("User name is taken");
        }

        User user = User.builder()
                .username(signupRequest.getUsername())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .firstName(signupRequest.getFirstName())
                .lastName(signupRequest.getLastName())
                .emailAddress(signupRequest.getEmail())
                .role(UserRole.USER).build();

        userRepository.save(user);
    }
}













