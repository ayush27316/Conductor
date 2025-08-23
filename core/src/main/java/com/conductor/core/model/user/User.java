package com.conductor.core.model.user;

import com.conductor.core.model.BaseEntity;
import com.conductor.core.model.Organization;
import com.conductor.core.model.ticket.Ticket;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**/
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User  extends BaseEntity{

    /*if user is an operator then its roles are
    * fetched from Operator table*/
    public enum UserType {
        ADMIN,
        OPERATOR,
        DEMO,
        /*limited access based on role*/
        API_KEY,
        PUBLIC
    }

    @Enumerated(EnumType.STRING)
    private UserType type;

    private  String username;
    private  String password;
    private  String firstName;
    private  String lastName;
    private  String emailAddress;
    private  String description;

}
