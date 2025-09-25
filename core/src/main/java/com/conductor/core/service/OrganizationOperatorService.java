package com.conductor.core.service;

import com.conductor.core.dto.RegisterEventManagerRequest;
import com.conductor.core.exception.EventNotFoundException;
import com.conductor.core.exception.UserNotFoundException;
import com.conductor.core.model.event.Event;
import com.conductor.core.model.permission.Permission;
import com.conductor.core.model.user.User;
import com.conductor.core.repository.EventRepository;
import com.conductor.core.repository.PermissionRepository;
import com.conductor.core.repository.UserRepository;
import com.conductor.core.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrganizationOperatorService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final PermissionRepository permissionRepository;
    private final PermissionService permissionService;

    //TO DO: email the benefited user
    public void registerManager(
            UserPrincipal grantedBy,
            RegisterEventManagerRequest request ) {
        User user = userRepository.findByExternalId(request.getUserExternalId()).orElseThrow(
                () -> new UserNotFoundException()
        );

        User grantedByUser = userRepository.findByExternalId(grantedBy.getUserExternalId()).get();

        Event event = eventRepository.findByExternalId(request.getEventExternalId()).orElseThrow(
                () -> new EventNotFoundException()
        );
        List<Permission> permissionList = user.getPermissions();
        Optional<Permission> targetPermissionOptional = permissionService.checkIfUserHasAccessToResourceByExternalId(
                user,
                request.getEventExternalId());
        if(targetPermissionOptional.isEmpty()){

            Permission p = Permission.builder()
                    .grantedTo(user)
                    .resource(event)
                    .grantedBy(grantedByUser)
                    .permission(Event.getOwnerPermission())
                    .build();
            permissionList.add(p);
            userRepository.save(user);

            return;
        }

        Permission targetPermission = targetPermissionOptional.get();

        //check if user already has this permission
        if(permissionService.checkIfUserHasPermission(user, request.getEventExternalId(), Event.getOwnerPermission())) {
            return;
        }
        //user does not have all the permission to become manager
        //of this event. Promotion!!
        //replace this permission to that of owner and persist

        targetPermission.setPermission(Event.getOwnerPermission());
        targetPermission.setGrantedBy(grantedByUser);

        userRepository.save(user);
    }
}
