package com.conductor.core.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@ConditionalOnMissingBean(ExternalIdentityProvider.class)
public class DefaultExternalIdentityProvider implements ExternalIdentityProvider {
    @Override
    public String generateId(Resource resource)
    {
        log.info("No ExternIdentityProvider Bean found! External Id's for all resources will be a random UUID");
        return UUID.randomUUID().toString();
    }
}
