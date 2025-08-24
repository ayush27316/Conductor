package com.conductor.core.repository;

import com.conductor.core.model.common.Permission;
import com.conductor.core.model.common.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing Permission entities.
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * Find permissions by user ID and active status.
     */
    List<Permission> findByUserIdAndIsActiveTrue(Long userId);

    /**
     * Find permissions by resource type and resource ID.
     */
    List<Permission> findByResourceAndResourceIdAndIsActiveTrue(Resource resource, Long resourceId);

    /**
     * Find permissions by user, resource, and privilege.
     */
    @Query("SELECT p FROM Permission p WHERE p.user.id = :userId AND p.resource = :resource " +
           "AND p.privilege = :privilege AND p.isActive = true " +
           "AND (p.resourceId = :resourceId OR p.resourceId IS NULL) " +
           "AND (p.resourceType = :resourceType OR p.resourceType IS NULL)")
    List<Permission> findByUserAndResourceAndPrivilege(@Param("userId") Long userId,
                                                       @Param("resource") Resource resource,
                                                       @Param("privilege") String privilege,
                                                       @Param("resourceId") Long resourceId,
                                                       @Param("resourceType") String resourceType);

    /**
     * Find permissions by user and resource type.
     */
    List<Permission> findByUserIdAndResourceAndIsActiveTrue(Long userId, Resource resource);

    /**
     * Find permissions by user, resource type, and resource ID.
     */
    List<Permission> findByUserIdAndResourceAndResourceIdAndIsActiveTrue(Long userId, Resource resource, Long resourceId);

    /**
     * Find all permissions for a specific resource instance.
     */
    @Query("SELECT p FROM Permission p WHERE p.resource = :resource AND p.resourceId = :resourceId " +
           "AND p.isActive = true")
    List<Permission> findByResourceInstance(@Param("resource") Resource resource, @Param("resourceId") Long resourceId);

    /**
     * Find inherited permissions for a user on a specific resource.
     */
    @Query("SELECT p FROM Permission p WHERE p.user.id = :userId AND p.isInherited = true " +
           "AND p.isActive = true AND p.resourceId = :resourceId")
    List<Permission> findInheritedPermissions(@Param("userId") Long userId, @Param("resourceId") Long resourceId);
}
