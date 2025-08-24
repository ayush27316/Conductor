package com.conductor.core.repository;

import com.conductor.core.model.common.Permission;
import com.conductor.core.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * Find all permissions for a specific user
     */
    List<Permission> findByUserAndIsActiveTrue(User user);

    /**
     * Find permissions for a user on a specific resource
     */
    List<Permission> findByUserAndResourceAndResourceIdAndIsActiveTrue(
            User user, String resource, String resourceId);

    /**
     * Find all permissions for a specific resource
     */
    List<Permission> findByResourceAndResourceIdAndIsActiveTrue(String resource, String resourceId);

    /**
     * Find expired permissions
     */
    @Query("SELECT p FROM Permission p WHERE p.expiresAt < :now AND p.isActive = true")
    List<Permission> findExpiredPermissions(@Param("now") ZonedDateTime now);

    /**
     * Find permissions granted by a specific user
     */
    List<Permission> findByGrantedByAndIsActiveTrue(User grantedBy);

    /**
     * Find permissions that will expire within a given time frame
     */
    @Query("SELECT p FROM Permission p WHERE p.expiresAt BETWEEN :start AND :end AND p.isActive = true")
    List<Permission> findPermissionsExpiringBetween(@Param("start") ZonedDateTime start, 
                                                   @Param("end") ZonedDateTime end);

    /**
     * Find permissions for a user on a specific resource with a specific privilege
     * This uses JSON path to search within the permissions map
     */
    @Query("SELECT p FROM Permission p WHERE p.user = :user " +
           "AND p.resource = :resource AND p.resourceId = :resourceId " +
           "AND p.isActive = true " +
           "AND JSON_EXTRACT(p.permissions, CONCAT('$.', :privilege)) IS NOT NULL")
    List<Permission> findByUserAndResourceAndResourceIdAndPrivilegeAndIsActiveTrue(
            @Param("user") User user, 
            @Param("resource") String resource, 
            @Param("resourceId") String resourceId, 
            @Param("privilege") String privilege);

    /**
     * Check if user has specific permission using JSON path
     */
    @Query("SELECT COUNT(p) > 0 FROM Permission p WHERE p.user = :user " +
           "AND p.resource = :resource AND p.resourceId = :resourceId " +
           "AND p.isActive = true " +
           "AND JSON_EXTRACT(p.permissions, CONCAT('$.', :privilege)) IS NOT NULL " +
           "AND (p.expiresAt IS NULL OR p.expiresAt > :now)")
    boolean hasPermission(@Param("user") User user, 
                         @Param("resource") String resource, 
                         @Param("resourceId") String resourceId, 
                         @Param("privilege") String privilege, 
                         @Param("now") ZonedDateTime now);

    /**
     * Find all users with a specific privilege on a resource
     */
    @Query("SELECT p.user FROM Permission p WHERE p.resource = :resource " +
           "AND p.resourceId = :resourceId AND p.isActive = true " +
           "AND JSON_EXTRACT(p.permissions, CONCAT('$.', :privilege)) IS NOT NULL")
    List<User> findUsersWithPrivilege(@Param("resource") String resource, 
                                     @Param("resourceId") String resourceId, 
                                     @Param("privilege") String privilege);
}
