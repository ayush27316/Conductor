package com.conductor.core.model.org;

import com.conductor.core.model.common.BaseEntity;
import com.conductor.core.model.user.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import java.util.List;


@Entity
@Table(name = "organization_permission")
public class OrganizationPermissionRegistry extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id_fk", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "organization_id_fk", nullable = false)
    private Organization organization;

    @Convert(converter = OrganizationPermissionListConverter.class)
    private List<OrganizationPermission> permissions;

    @Converter
    public class OrganizationPermissionListConverter implements AttributeConverter<List<OrganizationPermission>, String> {

        private static final ObjectMapper mapper = new ObjectMapper();

        @Override
        public String convertToDatabaseColumn(List<OrganizationPermission> attribute) {
            try {
                return attribute == null ? null : mapper.writeValueAsString(attribute);
            } catch (Exception e) {
                throw new IllegalArgumentException("Error converting permissions list to JSON", e);
            }
        }

        @Override
        public List<OrganizationPermission> convertToEntityAttribute(String dbData) {
            try {
                return dbData == null ? null :
                        mapper.readValue(dbData, new TypeReference<List<OrganizationPermission>>() {});
            } catch (Exception e) {
                throw new IllegalArgumentException("Error reading JSON permissions from DB", e);
            }
        }
    }

}
