package com.conductor.core.dto;

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
