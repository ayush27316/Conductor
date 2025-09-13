package com.conductor.core.service;

import com.conductor.core.model.Resource;
import com.conductor.core.model.file.File;
import com.conductor.core.model.user.User;
import com.conductor.core.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

public void storeFile(
        MultipartFile file,
        Optional<Resource> associatedResource,
        User uploadedBy)
{
    File entity = null;
    try {
        entity = File.builder()
                .resource(associatedResource.isEmpty()? null : associatedResource.get())
                .name(file.getOriginalFilename())
                .uploadedBy(uploadedBy)
                .contentType(file.getContentType())
                .size(file.getSize())
                .data(file.getBytes())
                .build();
    } catch (IOException e) {
        throw new RuntimeException(e);
    }

    fileRepository.save(entity);
    }

    public File getFile(String fileExternalId) {
        return fileRepository.findByExternalId(fileExternalId)
                .orElseThrow(() -> new RuntimeException("File not found"));
    }
}
