package com.conductor.core.model.application;

import com.conductor.core.model.common.BaseEntity;
import com.conductor.core.model.user.User;
import jakarta.persistence.*;
import lombok.*;


/**
 * Comments on {@link Application} from both users and reviewers
 */
@Entity
@Table(name = "application_comments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationComment extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "application_id_fk")
    private Application application;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id_fk")
    private User author;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

}
