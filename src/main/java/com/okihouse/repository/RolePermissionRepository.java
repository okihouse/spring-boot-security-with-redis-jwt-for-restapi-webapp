package com.okihouse.repository;

import com.okihouse.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by okihouse16 on 2018. 1. 29..
 */
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    @Query(value = "SELECT permission FROM ROLE_PERMISSION WHERE role_id IN (:roleIds)", nativeQuery = true)
    List<String> permissions(@Param("roleIds") List<Long> roleIds);

}
