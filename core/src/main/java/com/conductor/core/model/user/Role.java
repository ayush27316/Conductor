package com.conductor.core.model.user;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
/*
* we want to be able
* */

public enum Role {

    /*System-wide access to all resources */
    ADMIN("ADMIN", "Administrator", EnumSet.of(RoleTarget.ADMIN), AccessLevel.ALL),
    /*all-access to events owned by this organization*/
    OWNER("OWNER", "Organization owner", EnumSet.of(RoleTarget.USER), AccessLevel.ALL),
    /*all-access to specific events organized by this organizer*/
    ORGANIZER("EVENT_ORGANIZER", "organizes events", EnumSet.of(RoleTarget.USER), AccessLevel.RESTRICTED),
    /*restricted access to tickets for validation purposes only (eg. check-in machine)*/
    OPERATOR("OPERATOR", "operator", EnumSet.of(RoleTarget.API_KEY), AccessLevel.RESTRICTED);

    private  String roleName;
    private  String description;
    private  Set<RoleTarget> target;
    private  AccessLevel accessLevel;

    Role(String roleName, String description, Set<RoleTarget> target, AccessLevel accessLevel) {
        this.roleName = roleName;
        this.description = description;
        this.target = target;
        this.accessLevel=accessLevel;
    }

    public static Role fromRoleName(String roleName) {
        return Arrays.stream(values()).filter(r -> r.getRoleName().equals(roleName)).findFirst().orElse(OPERATOR);
    }

    public String getRoleName()
    {
        return roleName;
    }


    public enum RoleTarget{
        ADMIN, USER, API_KEY
    };

}
