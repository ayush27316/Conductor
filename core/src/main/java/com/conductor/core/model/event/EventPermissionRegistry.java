package com.conductor.core.model.event;

import com.conductor.core.model.common.BaseEntity;
import com.conductor.core.model.user.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;

import java.util.List;

/*At the controller level we need to annotate
* paths to resources and their minimum access level needed for the request to go
* through*/
@Entity
@Table(name = "event_permission")
public class EventPermissionRegistry extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id_fk", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id_fk", nullable = false)
    private Event event;

    @Convert(converter = EventPermissionListConverter.class)
    private List<EventPermission> permissions;

    @Converter
    public class EventPermissionListConverter implements AttributeConverter<List<EventPermission>, String> {

        private static final ObjectMapper mapper = new ObjectMapper();

        @Override
        public String convertToDatabaseColumn(List<EventPermission> attribute) {
            try {
                return attribute == null ? null : mapper.writeValueAsString(attribute);
            } catch (Exception e) {
                throw new IllegalArgumentException("Error converting permissions list to JSON", e);
            }
        }

        @Override
        public List<EventPermission> convertToEntityAttribute(String dbData) {
            try {
                return dbData == null ? null :
                        mapper.readValue(dbData, new TypeReference<List<EventPermission>>() {});
            } catch (Exception e) {
                throw new IllegalArgumentException("Error reading JSON permissions from DB", e);
            }
        }
    }

}
