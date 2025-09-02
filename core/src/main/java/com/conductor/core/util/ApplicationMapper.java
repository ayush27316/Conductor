package com.conductor.core.util;

import com.conductor.core.dto.ApplicationDTO;
import com.conductor.core.model.application.Application;
import com.conductor.core.model.application.ApplicationComment;
import com.conductor.core.model.common.ResourceType;
import com.conductor.core.service.EventApplicationService;

import java.util.List;
import java.util.stream.Collectors;

public class ApplicationMapper {


    public static ApplicationDTO toDto(Application application)
    {
        List<ApplicationDTO.Comment> commentDTOs = null;
        if (application.getComments() != null && !application.getComments().isEmpty()) {
            commentDTOs = application.getComments().stream()
                    .map(ApplicationMapper::toCommentDTO)
                    .collect(Collectors.toList());
        }

        ApplicationDTO dto = ApplicationDTO.builder()
                .submittedByUserExternalId(application.getSubmittedBy().getExternalId())
                .submittedAt(application.getSubmittedAt())
                .applicationStatus(application.getApplicationStatus())
                .processedByUserExternalId(application.getProcessedBy().getExternalId())
                .processedAt(application.getProcessedAt())
                .comments(commentDTOs)
                .applicationForm(application.getApplicationForm().getFormSchema())
                .applicationFormResponse(application.getApplicationFormResponse())
                .build();
        if(application.getTargetResource().getResourceType().equals(ResourceType.EVENT))
        {
             dto.setEventExternalId(application.getTargetResource().getExternalId());
        } else if (application.getTargetResource().getResourceType().equals(ResourceType.ORGANIZATION)) {
            dto.setOrganizationExternalId(application.getTargetResource().getExternalId());
        }

        return dto;
    }

    private static ApplicationDTO.Comment toCommentDTO(ApplicationComment comment) {
        if (comment == null) {
            return null;
        }
        return ApplicationDTO.Comment.builder()
                .authorExternalId(comment.getAuthor().getExternalId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
