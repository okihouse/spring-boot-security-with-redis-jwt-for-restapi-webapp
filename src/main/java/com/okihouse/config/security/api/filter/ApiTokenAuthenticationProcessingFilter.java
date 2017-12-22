package com.okihouse.config.security.api.filter;

import com.okihouse.config.security.api.token.ApiTokenFactory;
import com.okihouse.config.security.api.token.data.ApiTokenData;
import com.okihouse.config.security.common.handler.SecurityUserLoginHandler;
import com.okihouse.entity.User;
import com.okihouse.entity.UserRole;
import com.okihouse.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by okihouse16 on 2017. 12. 21..
 */
public class ApiTokenAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger logger = LoggerFactory.getLogger(ApiTokenAuthenticationProcessingFilter.class);

    public ApiTokenAuthenticationProcessingFilter(
            String processUrl
            ,UserRepository userRepository
            ,SecurityUserLoginHandler securityUserLoginHandler
            ,ApiTokenFactory apiTokenFactory) {
        super(processUrl);
        this.userRepository = userRepository;
        this.securityUserLoginHandler = securityUserLoginHandler;
        this.apiTokenFactory = apiTokenFactory;
    }

    private UserRepository userRepository;

    private SecurityUserLoginHandler securityUserLoginHandler;

    private ApiTokenFactory apiTokenFactory;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String accessToken = apiTokenFactory.extract(request.getHeader(ApiTokenData.AUTHORIZATION_HEADER_NAME));
        Map<String, Object> body = apiTokenFactory.getBody(accessToken);
        String principal = (String) body.get("sub"); // username

        /*
         * Do not use granted authority roles in token. (Must use the data that is stored in database or other storage.)
         * Granted authority can be changed by administrator at any time.
         */
        User user = userRepository.findFirstByUsername(principal);
        if(user == null){
            throw new UsernameNotFoundException("User is not exist!");
        }

        // Use the authorities of the user saved in database.
        List<UserRole> userRoles = user.getUserRoles();
        if (userRoles.isEmpty()) {
            throw new BadCredentialsException("Authentication Failed. User granted authority is empty.");
        }

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        userRoles.stream()
                .forEach(r -> grantedAuthorities.add(new SimpleGrantedAuthority(r.getRole())));

        logger.info("Api user attempt authentication. username={}, grantedAuthorities={}", principal, grantedAuthorities);
        return new UsernamePasswordAuthenticationToken(principal, null, grantedAuthorities); // No required credentials.
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);
        chain.doFilter(request, response);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        securityUserLoginHandler.onAuthenticationFailure(request, response, failed);
    }

}
