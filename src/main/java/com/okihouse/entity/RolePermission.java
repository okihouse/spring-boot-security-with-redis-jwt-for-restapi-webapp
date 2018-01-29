package com.okihouse.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by okihouse16 on 2017. 12. 21..
 */
@Data
@Entity
@Table(name = "ROLE_PERMISSION")
public class RolePermission {

    @Id
    @Column(name = "permissionId")
    private Long permissionId;

    @Column(name = "permission")
    private String permission;

    @ManyToOne
    @JoinColumn(name = "roleId")
    private Role role;

}
