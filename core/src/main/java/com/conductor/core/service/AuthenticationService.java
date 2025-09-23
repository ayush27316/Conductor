    package com.conductor.core.service;

    import com.conductor.core.dto.auth.LoginRequest;
    import com.conductor.core.dto.auth.SignupRequest;
    import com.conductor.core.exception.UsernameAlreadyTakenException;
    import com.conductor.core.model.user.User;
    import com.conductor.core.model.user.UserRole;
    import com.conductor.core.repository.UserRepository;
    import com.conductor.core.security.JwtUtil;
    import com.conductor.core.security.PrincipalPermission;
    import com.conductor.core.security.UserPrincipal;
    import com.conductor.core.util.PermissionMapper;
    import com.fasterxml.jackson.core.JsonProcessingException;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Service;

    import java.util.List;

    @Service
    @Slf4j
    @RequiredArgsConstructor
    public class AuthenticationService {

        private final AuthenticationManager authenticationManager;
        private final JwtUtil authUtil;
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final ObjectMapper objectMapper = new ObjectMapper();

        public String login(LoginRequest loginRequest){
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            User user =  (User) authentication.getPrincipal();


            UserPrincipal principal = UserPrincipal.builder()
                    .username(user.getUsername())
                    .userExternalId(user.getExternalId())
                    .organizationExternalId(user.getOrganization() != null ? user.getOrganization().getExternalId(): null)
                    .role(user.getRole())
                    .permissions(PermissionMapper.toPermissionPrincipal(user.getPermissions()))
                    .build();

            String token = authUtil.generateAccessToken(principal);
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













