package com.conductor.core.security;

import com.conductor.core.model.Option;
import com.conductor.core.model.ResourceType;
import com.conductor.core.model.permission.AccessLevel;
import com.conductor.core.model.permission.Privilege;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrincipalPermission {

    @JsonProperty("resource_type")
    private ResourceType resourceType;
    @JsonProperty("resource_id")
    private String resourceExternalId;

    @JsonSerialize(using = PermissionsSerializer.class)
    @JsonDeserialize(using = PermissionsDeserializer.class)
    private Map<Privilege, AccessLevel> permissions;

    @JsonProperty("granted_at")
    private ZonedDateTime grantedAt;
    @JsonProperty("granted_by_user_id")
    private String grantedByUserExternalId;
    @JsonProperty("expires_at")
    private ZonedDateTime expiresAt;

    public static class PermissionsSerializer extends JsonSerializer<Map<Privilege, AccessLevel>> {

        @Override
        public void serialize(Map<Privilege, AccessLevel> permission,
                              JsonGenerator jsonGenerator,
                              SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            for (Map.Entry<Privilege, AccessLevel> entry : permission.entrySet()) {
                jsonGenerator.writeStringField(entry.getKey().getName(), entry.getValue().getName());
            }
            jsonGenerator.writeEndObject();
        }

    }

    public static class PermissionsDeserializer extends JsonDeserializer<Map<Privilege, AccessLevel>> {

        @Override
        public Map<Privilege, AccessLevel> deserialize(
                JsonParser p,
                DeserializationContext ctxt) throws IOException, JacksonException {

            Map<Privilege, AccessLevel> result = new HashMap<>();
            if (p.currentToken() == null) {
                p.nextToken();
            }
            if (p.currentToken() != JsonToken.START_OBJECT) {
                ctxt.reportWrongTokenException(this, JsonToken.START_OBJECT, "Expected START_OBJECT");
            }

            while (p.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = p.getCurrentName(); // privilege name
                p.nextToken();
                String accessLevelStr = p.getText();

                Privilege privilege = new Privilege() {
                    @Override
                    public String getName() {
                        return fieldName;
                    }
                };

                AccessLevel accessLevel = Option.fromName(AccessLevel.class,accessLevelStr).get();

                result.put(privilege, accessLevel);
            }
            return result;
        }
    }
}
