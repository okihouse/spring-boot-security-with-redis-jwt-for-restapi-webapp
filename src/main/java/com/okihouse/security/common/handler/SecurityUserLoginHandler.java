package com.okihouse.security.common.handler;

import com.okihouse.security.api.token.ApiTokenFactory;
import com.okihouse.security.common.data.SecurityUrlData;
import com.okihouse.util.AjaxUtils;
import com.okihouse.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class SecurityUserLoginHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

    @Autowired
    private ApiTokenFactory tokenFactory;

    @Autowired
    private SecurityUrlData securityUrlData;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (AjaxUtils.isAjax(request) || AjaxUtils.isApi(request)) {
            Map<String, Object> map = new HashMap<>();
            map.put("success", true);
            if(AjaxUtils.isAjax(request)){
                map.put("redirect", securityUrlData.getMypage());
            } else {
                map.put("token", tokenFactory.createToken(authentication));
                map.put("refreshToken", tokenFactory.createRefreshToken(authentication));
                SecurityContextHolder.clearContext();
            }

            response.setStatus(HttpStatus.OK.value());
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            response.getWriter().write(JsonUtils.toJson(map));
            response.getWriter().flush();
        } else {
            response.sendRedirect(securityUrlData.getMypage());
        }
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
    	exception.printStackTrace();
    	if (AjaxUtils.isAjax(request) || AjaxUtils.isApi(request)) {
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("error", exception.getMessage());

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            response.getWriter().write(JsonUtils.toJson(map));
            response.getWriter().flush();
        } else {
            SecurityContextHolder.clearContext();
            String query = String.format("?error=%s", URLEncoder.encode(exception.getMessage(), "utf-8"));
            response.sendRedirect(securityUrlData.getLogin() + query);
        }
    }

}
