package com.conductor.core.service;

import com.conductor.core.model.permission.AccessLevel;
import com.conductor.core.model.permission.Privilege;
import com.conductor.core.model.user.User;
import com.conductor.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username:" + username)
                );

        log.info(
                user.getUsername() + ":" +
                user.getPermissions().stream().map(e -> {
                    var map = e.getPermission();
                    String s = "";
                    for (Map.Entry<Privilege, AccessLevel> entry: map.entrySet()){
                        s = s + "|" + entry.getKey().getName() + ":" + entry.getValue().getName()+ "|";
                    }
                    return s;
                }).collect(Collectors.toList()).toString());
        return user;
    }
}