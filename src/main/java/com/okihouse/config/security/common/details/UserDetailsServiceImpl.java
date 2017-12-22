package com.okihouse.config.security.common.details;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private PasswordEncoder encoder;

    /*
     * https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#remember-me
     * UserDetailsService is required for Remember-Me service.
     */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO: Find user in your database by username.
        String password = "$2a$11$BMHAHEws4Sz/SL5hhfLR/e45nehO.3RgV7j0v9mGaB9sYvEVHJb.e"; // TODO: Use the password of the user saved in database.

        if(StringUtils.isEmpty(username)){
            throw new UsernameNotFoundException("User is not exist!");
        }

        if (!encoder.matches(password, password)) {
            throw new BadCredentialsException("Authentication Failed. Username or Password not valid.");
        }

        // TODO: Use the authorities of the user saved in database.
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("USER"));
		return new User(username, password, grantedAuthorities);
	}

}
