package com.okihouse.security.common.provider;

import com.okihouse.entity.Role;
import com.okihouse.entity.User;
import com.okihouse.repository.RolePermissionRepository;
import com.okihouse.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by okihouse16 on 2017. 12. 21..
 */
@Component
public class SecurityAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = LoggerFactory.getLogger(SecurityAuthenticationProvider.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if(authentication == null){
            if(logger.isDebugEnabled()) {
                logger.debug("Authentication is null");
            }
            throw new AuthenticationServiceException("No authentication data provided");
        }

        String username = authentication.getName();
        Object credentials = authentication.getCredentials();
        if (!(credentials instanceof String)) {
            throw new AuthenticationServiceException("Authentication credentials is not valid");
        }
        String password = credentials.toString();

        // Find user in your database by username.
        User user = userRepository.findFirstByUsername(username);
        if(user == null){
            throw new UsernameNotFoundException("User is not exist!");
        }

        String savedUserPassword = user.getPassword(); // Use the password of the user saved in database.
        if (!encoder.matches(password, savedUserPassword)) {
            throw new BadCredentialsException("Authentication Failed. Username or Password not valid.");
        }

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

        return new UsernamePasswordAuthenticationToken(username, password, grantedAuthorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
