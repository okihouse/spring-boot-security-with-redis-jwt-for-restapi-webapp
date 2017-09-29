package com.example.config.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;

import com.example.config.security.handler.SecurityUserLoginHandler;

public class SecurityUserLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

	private static Logger logger = LoggerFactory.getLogger(SecurityUserLoginProcessingFilter.class);

    public SecurityUserLoginProcessingFilter(String defaultProcessUrl, SecurityUserLoginHandler securityLoginHandler, PersistentTokenBasedRememberMeServices rememberMeServices) {
        super(defaultProcessUrl);
        this.securityLoginHandler = securityLoginHandler;
        this.rememberMeServices = rememberMeServices;
    }

    private SecurityUserLoginHandler securityLoginHandler;

    private PersistentTokenBasedRememberMeServices rememberMeServices;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        if (!HttpMethod.POST.name().equals(request.getMethod())) {
            if(logger.isDebugEnabled()) {
                logger.debug("Authentication method not supported. Request method: " + request.getMethod());
            }
            throw new AuthenticationServiceException("Authentication method not supported");
        }

        String username = request.getParameter("username");
        if (StringUtils.isBlank(username)) {
            throw new AuthenticationServiceException("Login information is not provided");
        }
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, null);
        return this.getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
    		Authentication authentication) throws IOException, ServletException {
    	SecurityContextHolder.getContext().setAuthentication(authentication);
    	rememberMeServices.loginSuccess(request, response, authentication);
    	securityLoginHandler.onAuthenticationSuccess(request, response, authentication);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        securityLoginHandler.onAuthenticationFailure(request, response, failed);
    }

}
