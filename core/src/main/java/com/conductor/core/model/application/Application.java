package com.conductor.core.model.application;

import com.conductor.core.model.Resource;
import com.conductor.core.model.ResourceType;
import com.conductor.core.model.dispute.Dispute;
import com.conductor.core.model.event.Event;
import com.conductor.core.model.file.File;
import com.conductor.core.model.form.Form;
import com.conductor.core.model.org.Organization;
import com.conductor.core.model.user.User;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * {@link Application} is used to model long-lived transaction involved during
 * acquiring a Resource. An Application can be in multiple states as listed in
 * {@link ApplicationStatus}. Once application is in 'approved' state this usually
 * mean granting permissions to a new resource.
 *
 * In Conductor we use applications for the following:-
 * <p>
 *     <li>A user can apply to an {@link Event}. On approval get's a ticket to the event
 *          Through event's application organization's can set forms that must filled before
 *          a user can submit an application, this helps in the approval stage.
 *     </li>
 *     <li> A user can register new {@link Organization} by filling a form and submitting an application.
 *          Conductor team can verify the application, approve it and start the onboarding process.
 *     </li>
 *     <li> To manage disputes. Disputes can be caused within an organization, like for payments,
 *          permissions, or at system level, for example when a user gets blocked to apply for any
 *          event. Or for an organization to raise a concern. All this is handled by applications.
 *     </li>
 * </p>
 *
 *  @Note: Helpers methods have been defined to make it easy to work with this entity. It is required
 *      (but not validated at compile time) for all arguments to be not {@code null} and non-transient.
 *      Throws {@link PersistenceException} at runtime is not followed. Callee must save the application
 *      entity only for the changes to persist. And associated entity will be automatically handled based
 *      on Cascading configurations.
 *
 */
@Entity
@Table(name = "applications")
@Getter
@Setter
@NoArgsConstructor
public class Application extends Resource {

    /**
     * An Application has a polymorphic association with resources in the system.
     * This could either be an {@link Event} or {@link Organization} or a {@link Dispute}.
     */
    @OneToOne(
            optional = false,
            fetch = FetchType.LAZY)
    @JoinColumn(name = "target_resource_id_fk", nullable = false)
    private Resource targetResource;

    @ManyToOne(
            optional = false,
            fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by_user_id_fk", nullable = false)
    private User submittedBy;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApplicationStatus applicationStatus = ApplicationStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by_id_fk")
    private User processedBy;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @OneToMany(
            mappedBy = "application",
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            fetch = FetchType.EAGER)
    @OrderBy("createdAt ASC")
    private List<ApplicationComment> comments = new ArrayList<>();

    /**
     * Acquiring a resource often requires requester to submit information
     * that is necessary for the approval stage. Conductor provides {@link Form} 's
     * that can store form associated with the required information. The response
     * to this form can be stored here in String format. The access to the Form's
     * will be individually handled by the particular services using this Application.
     */
    @Lob
    @Column(name = "form_response")
    private String formResponse;

    @OneToMany(mappedBy = "resource")
    private List<File> files = new ArrayList<>();
    /**
     * Creates a new Application.
     *
     * @param targetResource target resource of this application. Must not be transient.
     * @param submittedBy user this application was submitted by. Must not be transient.
     * @param formResponse optional form response.
     * @return A new application.
     */
    public static Application createNew(
            Resource targetResource,
            User submittedBy,
            String formResponse)
    {
        Application application = new Application();

        application.setApplicationStatus(ApplicationStatus.PENDING);
        application.setTargetResource(targetResource);
        application.setSubmittedBy(submittedBy);
        application.setSubmittedAt(LocalDateTime.now());
        application.setFormResponse(formResponse);

        return application;
    }


    /**
     * Approve the application.
     * @param approvedBy user that approves this application
     * @throws IllegalStateException if the application is already in final state.
     *                               see {@link #isFinalStatus()} method.
     *
     */
    public void approve(User approvedBy) {
        validateStatusTransition(ApplicationStatus.APPROVED);
        this.applicationStatus = ApplicationStatus.APPROVED;
        processedBy = approvedBy;
        processedAt = LocalDateTime.now();
    }


    /**
     * Reject the application. Rejection reason if provided gets added as
     * a comment on the application.
     * @param rejectedBy user that rejects this application
     * @throws IllegalStateException if the application is already in final state.
     *                               see {@link #isFinalStatus()} method.
     *
     */
    public void reject(User rejectedBy,
                       String rejectionReason)
    {
        validateStatusTransition(ApplicationStatus.REJECTED);
        this.applicationStatus = ApplicationStatus.REJECTED;

        if(Objects.isNull(rejectionReason)){return;}

        ApplicationComment newComment = ApplicationComment.builder()
                .application(this)
                .author(rejectedBy)
                .content(rejectionReason).build();
        comments.add(newComment);
    }

    /**
     * Cancel the registration. Suppose to be done by user that
     * submitted the application.
     * @throws IllegalStateException if the application is already in final state.
     *                               see {@link #isFinalStatus()} method.
     */
    public void cancel() {
        validateStatusTransition(ApplicationStatus.CANCELLED);
        this.applicationStatus = ApplicationStatus.CANCELLED;
    }

    /**
     * Add a new comment to the application.
     */
    public void putComment(User author, String comment)
    {
        ApplicationComment newComment =
                ApplicationComment.builder()
                        .application(this)
                        .author(author)
                        .content(comment)
                        .build();

        comments.add(newComment);
    }

    /**
     * Check if registration is pending
     */
    public boolean isPending() {
        return ApplicationStatus.PENDING.equals(this.applicationStatus);
    }

    /**
     * Check if registration is approved
     */
    public boolean isApproved() {
        return ApplicationStatus.APPROVED.equals(this.applicationStatus);
    }

    /**
     * Check if application is in a final state
     */
    public boolean isFinalStatus() {
        return this.applicationStatus != null && this.applicationStatus.isFinalStatus();
    }

    /**
     * Validate that status transition is allowed
     */
    private void validateStatusTransition(ApplicationStatus newStatus) {
        if (this.applicationStatus != null && this.applicationStatus.isFinalStatus()) {
            throw new IllegalStateException(
                    String.format("Cannot transition from final status %s to %s",
                            this.applicationStatus, newStatus));
        }

        if (!this.applicationStatus.canBeProcessed() && newStatus != ApplicationStatus.CANCELLED) {
            throw new IllegalStateException(
                    String.format("Invalid status transition from %s to %s",
                            this.applicationStatus, newStatus));
        }
    }

    @PrePersist
    protected void prePersist() {
        super.init(ResourceType.APPLICATION, this);
    }

}