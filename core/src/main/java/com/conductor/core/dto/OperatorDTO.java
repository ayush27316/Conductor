package com.conductor.core.dto;

import com.conductor.core.model.Organization;
import com.conductor.core.model.user.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor  // Required for Jackson deserialization
@AllArgsConstructor
public class OperatorDTO {

    private  String username;
    private  String password;
    private  String firstName;
    private  String lastName;
    private  String emailAddress;

    private String organizationName;

}
