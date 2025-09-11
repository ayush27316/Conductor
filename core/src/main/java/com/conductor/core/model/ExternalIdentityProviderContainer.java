package com.conductor.core.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Used for injecting {@link ExternalIdentityProvider} into domain model.
 *
 * @see Resource
 * */
@Component
public class ExternalIdentityProviderContainer {
    private static ExternalIdentityProvider provider;

    @Autowired
    public ExternalIdentityProviderContainer(ExternalIdentityProvider provider) {
        ExternalIdentityProviderContainer.provider = provider;
    }

    public static ExternalIdentityProvider get() {
        return provider;
    }
}

