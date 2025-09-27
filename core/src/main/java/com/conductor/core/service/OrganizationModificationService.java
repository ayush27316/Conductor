package com.conductor.core.service;

import com.conductor.core.dto.OrganizationModificationRequest;
import com.conductor.core.dto.event.EventModification;
import com.conductor.core.exception.OrganizationNotFound;
import com.conductor.core.model.event.Event;
import com.conductor.core.model.org.Organization;
import com.conductor.core.repository.OrganizationRepository;
import com.conductor.core.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrganizationModificationService {

    private final OrganizationRepository organizationRepository;

    @Transactional
    public void modify(String organizationExternalId,
                       OrganizationModificationRequest request){

        Organization organization = organizationRepository.findByExternalId(organizationExternalId)
                .orElseThrow(
                        () -> new OrganizationNotFound()
                );

        applyModification(organization,request);

        organizationRepository.save(organization);
    }

    private void applyModification(
            Organization organization,
            OrganizationModificationRequest request) {

        Utils.updateIfNotNull(organization::setDescription, request.getDescription());
        Utils.updateIfNotNull(organization::setEmail, request.getEmail());
        Utils.updateIfNotNull(organization::setWebsiteUrl, request.getWebsiteUrl());
        Utils.updateIfNotNull(organization::setTags, request.getTags());
    }
}
