package com.okihouse.repository;

import com.okihouse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by okihouse16 on 2017. 12. 22..
 */
public interface UserRepository extends JpaRepository<User, Long> {

    User findFirstByUsername(String username);

}
