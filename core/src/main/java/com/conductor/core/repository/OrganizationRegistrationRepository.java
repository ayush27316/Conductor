package com.conductor.core.repository;

import com.conductor.core.model.org.OrganizationRegistration;
import com.conductor.core.model.user.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface OrganizationRegistrationRepository extends JpaRepository<OrganizationRegistration,Long> {

    Optional<OrganizationRegistration> findByUser(User user);
    Optional<OrganizationRegistration> findByUserAndStatus(User user, String status);

    boolean existsByUserAndStatus(User user, OrganizationRegistration.Status status);
    // For high concurrency scenarios
//   @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query("SELECT r FROM OrganizationRegistration r WHERE r.user = :user AND r.status = :status")
//    List<OrganizationRegistration> findByUserAndStatusWithLock(@Param("user") User user, @Param("status") OrganizationRegistration.Status status);

    Optional<OrganizationRegistration> findByRegistrationId(String registrationId);

    List<OrganizationRegistration> findByStatus(OrganizationRegistration.Status status);
}
