package com.conductor.core.model.permission;

import com.conductor.core.model.Resource;
import com.conductor.core.model.org.Organization;

import java.util.List;

// Note: This class is not in use for the current version
public class OperatorRole extends Resource {

    private String roleName;

    //need to serialise this manually
    //as we persist these permissions only when a role is assigned to an operator
    private List<Permission> permissions;

    private Organization organization;

}
