package com.okihouse.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

/**
 * Created by okihouse16 on 2017. 12. 21..
 */
@Data
@Entity
@Table(name = "ROLE")
public class Role {

    @Id
    @Column(name = "roleId")
    private Long id;

    @Column(name = "role")
    private String role;

    @ManyToOne
    @JoinColumn(name = "id")
    private User user;

    @OneToMany(mappedBy = "role", fetch = FetchType.EAGER ,cascade = CascadeType.PERSIST)
    private List<RolePermission> permissions;

}
