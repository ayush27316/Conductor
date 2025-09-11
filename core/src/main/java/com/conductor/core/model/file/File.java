package com.conductor.core.model.file;

import com.conductor.core.model.BaseEntity;
import com.conductor.core.model.Resource;
import com.conductor.core.model.ResourceType;
import com.conductor.core.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

/**
 *
 * Access to file are usually association based that is why
 * it doesn't extend a resource, but it technically is.
 */
@Entity
@Table(name = "files")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class File extends Resource {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id_fk", nullable = false)
    private Resource resource;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_user_id_fk")
    private User uploadedBy;

    @Column(nullable = false)
    private String name;

    @Column(name = "size")
    private Long size;

    @Column(name = "content_type")
    private String contentType;

    @Lob
    @Column(name = "data")
    private byte[] data;


    public void setFileData(byte[] data) {
        this.data = data;
        this.size = data != null ? (long) data.length : 0L;
    }

    @PrePersist
    void init(){
        super.init(ResourceType.FILE,null);
    }
}