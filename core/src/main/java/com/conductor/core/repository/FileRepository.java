package com.conductor.core.repository;

import com.conductor.core.model.file.File;
import com.sun.jdi.LongValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
    Optional<File> findByExternalId(String externalId);
}
