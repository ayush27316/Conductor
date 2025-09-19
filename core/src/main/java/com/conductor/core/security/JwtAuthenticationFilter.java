package com.conductor.core.security;

import com.conductor.core.dto.permission.PermissionDTO;
import com.conductor.core.exception.TokenNotValidException;
import com.conductor.core.model.user.User;
import com.conductor.core.model.user.UserRole;
import com.conductor.core.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final HandlerExceptionResolver handlerExceptionResolver;

    /**/
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();

        try {
            if (path.startsWith("/auth/") || path.startsWith("/public/") || path.startsWith("/h2-console/")) {
                // skip JWT auth for public endpoints
                filterChain.doFilter(request, response);
                return;
            }

            log.debug("Processing JWT authentication for request: {}", request.getRequestURI());

            final String requestTokenHeader = request.getHeader("Authorization");
            if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
                String token = requestTokenHeader.substring(7);


                if(!jwtUtil.validateToken(token)){
                    throw new TokenNotValidException("Token invalid");
                };

                try {
                    String userExternalId = jwtUtil.getExternalId(token);
                    UserRole userRole = jwtUtil.getUserRole(token);
                    List<PermissionDTO> permissionDTOS = jwtUtil.getPermissions(token);

                    if (userExternalId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserPrincipal userPrincipal = UserPrincipal.builder()
                                .externalId(userExternalId)
                                .role(userRole)
                                .permissions(permissionDTOS)
                                .build();

                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        userPrincipal,
                                        null,
                                        userPrincipal.getAuthorities()
                                );

                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                } catch (Exception e) {
                    log.warn("JWT token validation failed: {}", e.getMessage());
                    // Continue without authentication - let Spring Security handle it
                }
            }

            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            log.error("Error in JWT filter: {}", ex.getMessage(), ex);
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }
    }
}