package com.okihouse.security.common.details;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.okihouse.entity.Role;
import com.okihouse.entity.User;
import com.okihouse.repository.RolePermissionRepository;
import com.okihouse.repository.UserRepository;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    /*
     * https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#remember-me
     * UserDetailsService is required for Remember-Me service.
     */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Find user in your database by username.
        User user = userRepository.findFirstByUsername(username);
        if(user == null){
            throw new UsernameNotFoundException("User is not exist!");
        }
        
        String password = user.getPassword(); // Use the password of the user saved in database.
        
        // Use the authorities of the user saved in database.
        List<Role> roles = user.getRoles();
        if (roles.isEmpty()) {
            throw new BadCredentialsException("Authentication Failed. User granted authority is empty.");
        }

        List<Long> roleIds = roles.stream()
                                  .map(Role::getId)
                                  .collect(Collectors.toList());

        List<String> permissions = rolePermissionRepository.permissions(roleIds);

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        permissions.stream()
                   .forEach(p -> grantedAuthorities.add(new SimpleGrantedAuthority(p)));
		return new org.springframework.security.core.userdetails.User(username, password, grantedAuthorities);
	}

}
