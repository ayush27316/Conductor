package com.conductor.core.service;

//import com.conductor.core.util.PermissionMapper;
import com.conductor.core.model.Resource;
import com.conductor.core.model.permission.AccessLevel;
import com.conductor.core.model.permission.Permission;
import com.conductor.core.model.permission.Privilege;
import com.conductor.core.model.user.User;
import com.conductor.core.repository.PermissionRepository;
import com.conductor.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    //very generic; we need to know if there was an entry for this resource at all
    //and if there is then what are we missing
    public boolean checkIfUserHasPermission(
            User user,
            String resourceExternalId,
            Map<Privilege,AccessLevel> permissions)
    {

        Optional<Permission> permissionOptional = checkIfUserHasAccessToResourceByExternalId(
                user,
                resourceExternalId);

        if(permissionOptional.isEmpty()){
            return false;
        }
        Permission p = permissionOptional.get();

        for(Map.Entry<Privilege,AccessLevel> entry: permissions.entrySet()) {
            boolean found = false;
            for(Map.Entry<Privilege,AccessLevel> pEntry: p.getPermission().entrySet()){
                if(
                        entry.getKey().equals(pEntry.getKey())
                        && entry.getValue().equals(pEntry.getValue()))
                {
                    found = true;
                }
            }
            if(found == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return {@link Permission} object with externalId as
     * resourceExternalId if it exists
     */
    public Optional<Permission> checkIfUserHasAccessToResourceByExternalId(
            User user,
            String resourceExternalId){
        List<Permission> permissions = user.getPermissions();
        boolean found = false;
        for (Permission p: permissions){
            if(p.getResource().getExternalId().equals(resourceExternalId)){
                found = true;
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    public Map<Privilege,AccessLevel> merge(Map<Privilege,AccessLevel> map1, Map<Privilege,AccessLevel> map2 ){
        return null;
    }
}
