package com.conductor.core.model.permission;

import com.conductor.core.model.common.Option;
import com.conductor.core.model.common.ResourceType;
import com.conductor.core.model.event.EventPrivilege;
import com.conductor.core.model.org.OrganizationPrivilege;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Converter
public class PermissionConverter implements AttributeConverter<PermissionMap, String> {

    private static final ObjectMapper mapper = new ObjectMapper();


    @Override
    public String convertToDatabaseColumn(PermissionMap attribute) {

        if (attribute == null || attribute.getPermission().isEmpty()) {
            return null;
        }
        try {
            // Convert enum keys to strings
            Map<String, String> stringMap = new HashMap<>();
            stringMap.put("resourceType",attribute.getResourceType().toString());
            for (Map.Entry<Privilege, AccessLevel> entry : attribute.getPermission().entrySet()) {
                stringMap.put(entry.getKey().getName(), entry.getValue().getName());
            }
            return mapper.writeValueAsString(stringMap);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting Map to JSON: " + e.getMessage(), e);
        }
    }

    @Override
    public PermissionMap convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return new PermissionMap();
        }
        try {
            Map<String, String> stringMap = mapper.readValue(dbData, new TypeReference<Map<String, String>>() {});

            String resourceTypeName = stringMap.remove("resourceType");
            ResourceType resourceType = Option.fromName(ResourceType.class, resourceTypeName)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown resource type: " + resourceTypeName));

            Map<Privilege, AccessLevel> permissions = new HashMap<>();

            for (Map.Entry<String, String> entry : stringMap.entrySet()) {

                Privilege privilege = switch (resourceType) {
                    case ORGANIZATION -> Option.fromName(OrganizationPrivilege.class, entry.getKey())
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Unknown privilege: " + entry.getKey() + " for resource type: " + resourceType));
                    case EVENT -> Option.fromName(EventPrivilege.class, entry.getKey())
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Unknown privilege: " + entry.getKey() + " for resource type: " + resourceType));
                    default -> null;
                };


                AccessLevel accessLevel = Option.fromName(AccessLevel.class, entry.getValue())
                        .orElseThrow(() -> new IllegalArgumentException("Unknown access level: " + entry.getValue()));

                permissions.put(privilege, accessLevel);
            }

            return PermissionMap.builder()
                    .resourceType(resourceType)
                    .permission(permissions)
                    .build();

        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting JSON to PermissionMap: " + e.getMessage() + " - Data: " + dbData, e);
        }
    }



//    private <E extends Enum<E> & Option> Class<E> getPrivilegeClassForResourceType(ResourceType resourceType) {
//        switch (resourceType) {
//            case ORGANIZATION: return (Class<E>) OrganizationPrivilege.class;
//            case EVENT: return (Class<E>) EventPrivilege.class;
//            case USER: throw new IllegalArgumentException("USER resource type not yet supported");
//            case OPERATOR: throw new IllegalArgumentException("OPERATOR resource type not yet supported");
//            case TICKET: throw new IllegalArgumentException("TICKET resource type not yet supported");
//            default: throw new IllegalArgumentException("Unsupported resource type: " + resourceType);
//        }
//    }
}



//    @Override
//    public PermissionMap convertToEntityAttribute(String dbData) {
//        if (dbData == null || dbData.trim().isEmpty()) {
//            return new PermissionMap();
//        }
//        try {
//            // First read as string map, then convert back to enums
//            Map<String, String> stringMap = mapper.readValue(dbData, new TypeReference<Map<String, String>>() {});
//
//            ResourceType resourceType = Option.fromName(ResourceType.class, stringMap.get("resourceType")).get();
//
//
//            Map<Privilege, AccessLevel> enumMap = new HashMap<>();
//
//
//
//            for (Map.Entry<String, String> entry : stringMap.entrySet()) {
//
//                //error here
//                var privilege = Option.fromName(getPrivilegeClassForResourceType(resourceType), entry.getKey()).get();
//
//                AccessLevel accessLevel = Option.fromName(AccessLevel.class, entry.getValue()).get();
//                enumMap.put(privilege, accessLevel);
//            }
//
//            return enumMap;
//        } catch (Exception e) {
//            throw new IllegalArgumentException("Error converting JSON to Map: " + e.getMessage() + " - Data: " + dbData, e);
//        }
//    }
//
//    private Class<? extends Privilege> getPrivilegeClassForResourceType(ResourceType resourceType) {
//        switch (resourceType) {
//            case ORGANIZATION: return OrganizationPrivilege.class;
//            case EVENT: return EventPrivilege.class;
//            case USER: throw new IllegalArgumentException("USER resource type not yet supported");
//            case OPERATOR: throw new IllegalArgumentException("OPERATOR resource type not yet supported");
//            case TICKET: throw new IllegalArgumentException("TICKET resource type not yet supported");
//            default: throw new IllegalArgumentException("Unsupported resource type: " + resourceType);
//        }
//    }
//
