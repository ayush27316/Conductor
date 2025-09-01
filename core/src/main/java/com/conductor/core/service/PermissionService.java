package com.conductor.core.service;

//import com.conductor.core.util.PermissionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PermissionService {

//    private final PermissionRepository permissionRepository;
//    private final UserRepository userRepository;
//    //private final PermissionMapper permissionMapper;
//    private final OrganizationRepository organizationRepository;
//    private final EventRepository eventRepository;
//    private final TicketReservationRepository ticketReservationRepository;
//    /**
//     * Grant permissions to a user
//     */
//    @Transactional
//    public GrantPermissionResponseDTO grantPermission(GrantPermissionRequestDTO request) {
//        //not added granted by
//        //use secuirty context for that
//        User user = userRepository.findByExternalId(request.getBUserExternalId())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//
//
//        List<Permission> existingPermissions = permissionRepository
//                .findByUserAndResourceNameAndResourceId(user, request.getResourceName(), request.getResourceId());
//
//        Pair<Boolean, String> result = validate(request.getResourceName(), request.getPermissions());
//        if(!result.getStatus()){
//            return new GrantPermissionResponseDTO(result.getMessage());
//
//        }
//        Permission permission;
//
//        if (!existingPermissions.isEmpty()) {
//            if(!isValidResourceExternalId(ResourceType.fromName(request.getResourceName()).get(),request.getResourceId())){
//                return new GrantPermissionResponseDTO("ResourceType: "+ request.getResourceName()+ " with" + request.getResourceId()+ " not found");
//            }
//            permission = Permission.builder()
//                    .user(user)
//                    .privileges(request.getPermissions())
//                    .resourceName(request.getResourceName())
//                    .expiresAt(request.getExpiresAt())
//                    .build();
//
//        } else {
//            // if exisiting permissions is not empty this
//            //by design mean there will only be a single row of permission
//            //for this user on this resoruce
//
//
//            permission = existingPermissions.get(0);
//
//            //merge the exisitng permissions with new ones
//            Map<String, String> currentPrivileges = permission.getPrivileges();
//            Map<String, String> updatedPrivileges = mergePrevileges(
//                    currentPrivileges, request.getPermissions());
//
//            permission.setPrivileges(updatedPrivileges);
//
//        }
//
//        permissionRepository.save(permission);
//
//        return new GrantPermissionResponseDTO("Permissions granted succesfully");
//    }
//
//    private boolean isValidResourceExternalId(ResourceType resourceType, String resourceId) {
//
//        //verify if resourceType with the given external id exists
//        switch (resourceType){
//            case ORGANIZATION :
//                if(!organizationRepository.findByExternalId(resourceId).isEmpty())
//                {return true;}
//            case EVENT:
//                if(!eventRepository.findByExternalId(resourceId).isEmpty())
//                {return true;}
//        }
//        return false;
//    }
//
//    /**
//     * Validates if the given resourceName and permissions are valid.
//     *
//     * @param resourceName the name of the resourceType (e.g. "organization", "event")
//     * @param permissions  a map of privilege -> accessLevel
//     * @return true if valid, false otherwise
//     */
//    /*since permissions, privileges, and accesslevel are issued by the system and encoded in jwt
//    * whenever the  jwt is verified permissions are boound to be valid */
//    private  Pair<Boolean, String> validate(String resourceName, Map<String, String> permissions) {
//        Optional<ResourceType> resourceOpt = ResourceType.fromName(resourceName);
//        //we should probably throw error like ResourceNotFoundError
//        if (resourceOpt.isEmpty()) {
//            return Pair.of(false, resourceName +" does not exist"); // invalid resourceType
//        }
//
//        ResourceType resourceType = resourceOpt.get();
//
//        for (Map.Entry<String, String> entry : permissions.entrySet()) {
//            String privilegeName = entry.getKey();
//            String accessName = entry.getValue();
//
//            // validate privilege depending on resourceType
//            boolean validPrivilege = switch (resourceType) {
//                case ORGANIZATION -> OrganizationPrivilege.fromName(privilegeName).isPresent();
//                case EVENT -> EventPrivilege.fromName(privilegeName).isPresent();
//                case USER, TICKET -> true;
//            };
//
//            if (!validPrivilege) {
//                return Pair.of(false, privilegeName + "does not exist");
//            }
//
//            // validate access level
//            Optional<AccessLevel> accessOpt = AccessLevel.fromName(accessName);
//            if (accessOpt.isEmpty()) {
//                return Pair.of(false, accessName + "is not valid");
//            }
//
//            // Example: if AUDIT privilege → only READ allowed
//            if (privilegeName.equalsIgnoreCase("audit") && accessOpt.get() != AccessLevel.READ) {
//                return Pair.of(false, " Privilege 'audit' can only have read access");
//            }
//        }
//
//        return Pair.of(true, "");
//    }
//
//    /*
//    * Returns An optional list of permissions this user has.
//    * An empty option mean that user can only access publicly
//    * available resources.
//    * */
//    @Transactional
//    public Optional<List<Permission>> getUserPermissions(User user){
//        List<Permission> permissions = permissionRepository.findByUser(user);
//
//        if(permissions.isEmpty())
//        {
//            return Optional.empty();
//        }
////        List<PermissionDTO> permissionDTOS = new ArrayList<>();
////        for(Permission p : permissions){
////            permissionDTOS.add(PermissionDTO.builder()
////                    .userExternalId(p.getUser().getExternalId())
////                    .resourceId(p.getResourceId())
////                    .resourceName(p.getResourceName())
////                    .permissions(p.getPermissions())
////                    .build());
////        }
//        return Optional.of(permissions);
//    }
//
//    public List<Permission> createRequiredPermissions(String resourceName, String resourceId, Map<String, String> privileges) {
//        Permission permission = Permission.builder()
//                .resourceName(resourceName)
//                .resourceId(resourceId)
//                .privileges(privileges)
//                .build();
//
//        return List.of(permission);
//    }
//
//    // More specific helper methods
//    public List<Permission> requireReadAccess(String resourceName) {
//        return createRequiredPermissions(resourceName, null, Map.of("READ", "READ"));
//    }
//
//    public List<Permission> requireWriteAccess(String resourceName, String resourceId) {
//        return createRequiredPermissions(resourceName, resourceId, Map.of("WRITE", "WRITE"));
//    }
//
//    public List<Permission> requireDeleteAccess(String resourceName, String resourceId) {
//        return createRequiredPermissions(resourceName, resourceId, Map.of("DELETE", "DELETE"));
//    }
//
//    /**
//     * Revoke a specific permission from a user
//     */
//    public void revokePermission(RevokePermissionRequestDTO request) {
//        Pair<Boolean, String> result = validate(request.getResourceName(), request.getPermissions());
//        if(!result.getStatus()){
//           return;
//        }
//
//        if(!isValidResourceExternalId(ResourceType.fromName(request.getResourceName()).get(),request.getResourceId())){
//            return;
//        }
//
//        List<Permission> permissions = permissionRepository
//                .findByUserAndResourceNameAndResourceId(
//                        userRepository.findByExternalId(request.getTargetUserExternalId()).orElse(null),
//                        request.getResourceName(),
//                        request.getResourceId());
//
//        Permission permission = permissions.get(0);
//        Map<String, String> currentPermissions = permission.getPrivileges();
//        Map<String, String> updatedPermissions = diffPermissions(currentPermissions, request.getPermissions());
//
//        permission.setPrivileges(updatedPermissions);
//        permissionRepository.save(permission);
//    }
//
//    /**
//     * Merge multiple permission maps
//     */
//    public Map<String, String> mergePrevileges(Map<String, String>... permissionMaps) {
//        Map<String, String> merged = new HashMap<>();
//        for (Map<String, String> permissions : permissionMaps) {
//            if (permissions != null) {
//                merged.putAll(permissions);
//            }
//        }
//        return merged;
//    }
//    /**
//     * Returns a new map that is the difference of mapA and mapB.
//     * i.e. all entries from mapA except those whose keys exist in mapB.
//     */
//    public Map<String, String> diffPermissions(Map<String, String> mapA, Map<String, String> mapB) {
//        if (mapA == null) {
//            return Collections.emptyMap();
//        }
//
//        Map<String, String> result = new HashMap<>(mapA);
//        if (mapB != null) {
//            for (String key : mapB.keySet()) {
//                result.remove(key);
//            }
//        }
//        return result;
//    }
//


}
