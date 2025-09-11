package com.conductor.core.model.permission;

import com.conductor.core.model.Option;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.HashMap;
import java.util.Map;

@Converter
public class PermissionConverter implements AttributeConverter<Map<Privilege,AccessLevel>, String> {

    private static final ObjectMapper mapper = new ObjectMapper();


    @Override
    public String convertToDatabaseColumn(Map<Privilege, AccessLevel> attribute) {

        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        try {
            // Convert enum keys to strings
            Map<String, String> stringMap = new HashMap<>();

            for (Map.Entry<Privilege, AccessLevel> entry : attribute.entrySet()) {
                stringMap.put(entry.getKey().getName(), entry.getValue().getName());
            }
            return mapper.writeValueAsString(stringMap);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting Map to JSON: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<Privilege, AccessLevel> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return new HashMap<>();
        }
        try {
            Map<String, String> stringMap = mapper.readValue(dbData, new TypeReference<Map<String, String>>() {
            });
            Map<Privilege, AccessLevel> permissions = new HashMap<>();

            for (Map.Entry<String, String> entry : stringMap.entrySet()) {

                // !!!!!arguable the best hack!!!!!
                // When a privilege is persisted it is guaranteed that what got persisted
                // is in fact string name of one of the privilege enums.  This allows us
                // to retrieve the name in an empty wrapper Privilege which can then be
                // safeCast to appropriate privilege enum whenever application layer needs it.
                // safeCast is different from normal cast in the sense that it will cast to a given
                // target type based on name of the privilege. This introduces 2  constrains
                // that must be followed:
                // 1: For a specific enum each name of differnet options must be unique. There
                // will be no compile time exception thrown for overlapping name but this will
                // result safeCast to misbehave. Which will generally give you the first matched option
                // in the targetPrivilege class.
                // 2: Always use safeCase in order to get actual enum privilege specific to a resource.
                //what is retrieved from the database is not a actual enum privilege type casted to
                //Privilege type but just a wrapper that has no underlying enum class at all. The
                Privilege privilege = new Privilege() {
                    @Override
                    public String getName() {
                        return entry.getKey();
                    }
                };

                AccessLevel accessLevel = Option.fromName(AccessLevel.class, entry.getValue())
                        .orElseThrow(() -> new IllegalArgumentException("Unknown access level: " + entry.getValue()));

                permissions.put(privilege, accessLevel);
            }

            return permissions;

        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting JSON to Map<Privilege,AccessLevel>: " + e.getMessage() + " - Data: " + dbData, e);
        }
    }
}
