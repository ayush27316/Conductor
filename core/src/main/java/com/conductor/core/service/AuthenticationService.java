package com.conductor.core.service;

import com.conductor.core.dto.auth.LoginRequestDTO;
import com.conductor.core.dto.auth.LoginResponseDTO;
import com.conductor.core.dto.auth.SignUpRequestDTO;
import com.conductor.core.dto.auth.SignUpResponseDTO;
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

    public LoginResponseDTO login(LoginRequestDTO loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword())
            );

        User user =  (User) authentication.getPrincipal();

        String token = authUtil.generateAccessToken(user);
        return new LoginResponseDTO(token);
    }


    public SignUpResponseDTO signup(SignUpRequestDTO signupRequestDto) {

        if(!userRepository.findByUsername(signupRequestDto.getUsername()).isEmpty()){
            throw new UsernameAlreadyTakenException("User name is taken");
        }

        User user = User.builder()
                .username(signupRequestDto.getUsername())
                .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                .firstName(signupRequestDto.getFirstName())
                .lastName(signupRequestDto.getLastName())
                .emailAddress(signupRequestDto.getEmail())
                .role(UserRole.USER).build();
        
        userRepository.save(user);

        return new SignUpResponseDTO(user.getExternalId().toString(), "User signup successful");
    }
}














