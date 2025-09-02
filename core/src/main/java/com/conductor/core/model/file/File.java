package com.conductor.core.model.file;

import com.conductor.core.model.application.Application;
import com.conductor.core.model.application.ApplicationStatus;
import com.conductor.core.model.common.BaseEntity;
import com.conductor.core.model.common.Resource;
import com.conductor.core.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

/**
 * Access to file are ususally association based that is why
 * it doesn't extend a resource but it technically is.
 */
@Entity
@Table(name = "files")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class File extends BaseEntity {


    @Column(name = "external_id", nullable = false, updatable = false, unique = true)
    @Builder.Default
    private String externalId = UUID.randomUUID().toString();


    @ManyToOne
    @JoinColumn(name = "resource_id_fk", nullable = false)
    private Resource resource;


    @ManyToOne(optional = false)
    @JoinColumn(name = "uploaded_by_user_id_fk")
    private User uploadedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false)
    private FileType fileType;

    @Column(nullable = false)
    private String name;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "mime_type")
    private String mimeType;

    @Lob
    @Column(name = "file_data_blob")
    private byte[] fileDataBlob;

    @PrePersist
    public void ensureExternalId() {
        if (externalId == null) {
            externalId = UUID.randomUUID().toString();
        }

        // Set default MIME types based on file type
        if (mimeType == null) {
            switch (fileType) {
                case PDF:
                    mimeType = "application/pdf";
                    break;
                case PNG:
                    mimeType = "image/png";
                    break;
            }
        }
    }


    public void setFileData(byte[] data) {
        this.fileDataBlob = data;
        this.fileSize = data != null ? (long) data.length : 0L;
    }

}