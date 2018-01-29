package com.okihouse.entity;

import java.util.List;

import javax.persistence.*;

import lombok.Data;

/**
 * Created by okihouse16 on 2017. 12. 21..
 */
@Data
@Entity
@Table(name="USER")
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "username")
    private String username;
    
    @Column(name = "password")
    private String password;
    
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER ,cascade = CascadeType.PERSIST)
    private List<Role> roles;


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '}';
    }
}
