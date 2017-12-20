package com.example.config.security.implement;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		boolean exist = true; // TODO: Find user in your database by username.
		if(!exist){
            throw new UsernameNotFoundException("User is not exist!");
        }

		List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER")); // TODO: Use the permissions of the user saved in database.
        String password = "$2a$11$BMHAHEws4Sz/SL5hhfLR/e45nehO.3RgV7j0v9mGaB9sYvEVHJb.e"; // TODO: Use the password of the user saved in database.
		UserDetails userDetails = new User(username, password, authorities);
		return userDetails;
	}

}
