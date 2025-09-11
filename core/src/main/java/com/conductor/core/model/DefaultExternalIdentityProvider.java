package com.conductor.core.model;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
//@ConditionalOnMissingBean(ExternalIdentityProvider.class)
public class DefaultExternalIdentityProvider implements ExternalIdentityProvider {
    @Override
    public String generateId(ResourceType type, Object info) {
        return UUID.randomUUID().toString();
    }
}
