package com.conductor.core.model;

import jakarta.persistence.*;

//@Entity
//@Table(name = "organization_onboarding_status")
public class OrganizationOnboardingStatus {
    public enum Status{
        NOTIFIED,
        BOARDED
    }
//
//    @Enumerated(EnumType.STRING)
//    private Status status;
//
//    @OneToOne
//    private Organization organization;
}
