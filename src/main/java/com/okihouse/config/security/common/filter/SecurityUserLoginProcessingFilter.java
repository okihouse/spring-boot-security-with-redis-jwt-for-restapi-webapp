package com.okihouse.config.security.common.filter;

import java.io.IOException;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.okihouse.util.AjaxUtils;
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

import com.okihouse.config.security.common.handler.SecurityUserLoginHandler;
import com.okihouse.util.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Created by okihouse16 on 2017. 12. 21..
 */
public class SecurityUserLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private static Logger logger = LoggerFactory.getLogger(SecurityUserLoginProcessingFilter.class);

    public SecurityUserLoginProcessingFilter (
            String processUrl
            ,SecurityUserLoginHandler securityUserLoginHandler
            ,PersistentTokenBasedRememberMeServices rememberMeServices) {
        super(processUrl);
        this.securityUserLoginHandler = securityUserLoginHandler;
        this.rememberMeServices = rememberMeServices;
    }

    public SecurityUserLoginProcessingFilter (
            String processUrl
            ,SecurityUserLoginHandler securityUserLoginHandler) {
        super(processUrl);
        this.securityUserLoginHandler = securityUserLoginHandler;
    }

    private boolean isRememberMe = false;

    private SecurityUserLoginHandler securityUserLoginHandler;

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
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("remember-me");

        if(AjaxUtils.isAjax(request) || AjaxUtils.isApi(request)){
            Map<String, String> userContext = JsonUtils.fromJson(request.getReader(), new TypeReference<Map<String,String>>(){});
            username = userContext.get("username");
            password = userContext.get("password");
        }

        if(StringUtils.isNotBlank(rememberMe)){
            this.isRememberMe = true;
        }

        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new AuthenticationServiceException("username or password is not valid.");
        }
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        logger.info("User attempt authentication. username={}", username);
        return this.getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authentication) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        if (this.isRememberMe) {
            if(logger.isDebugEnabled()) {
                logger.debug("User remember-me service is activated.");
            }
            rememberMeServices.loginSuccess(request, response, authentication);
        }
        securityUserLoginHandler.onAuthenticationSuccess(request, response, authentication);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        securityUserLoginHandler.onAuthenticationFailure(request, response, failed);
    }
}
