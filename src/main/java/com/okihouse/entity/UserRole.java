package com.okihouse.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by okihouse16 on 2017. 12. 21..
 */
@Data
@Entity
@Table(name = "USER_ROLE")
public class UserRole {

    @Id
    @Column(name = "roleId")
    private Long id;

    @Column(name = "role")
    private String role;

    @ManyToOne
    @JoinColumn(name = "id")
    private User user;

}
