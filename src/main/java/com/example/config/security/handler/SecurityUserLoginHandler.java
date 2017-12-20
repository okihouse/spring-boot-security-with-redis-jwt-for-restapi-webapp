package com.example.config.security.handler;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.example.config.security.url.SecurityUrlInformation;
import com.example.util.AjaxUtils;
import com.example.util.JsonUtils;

import lombok.Data;

@Configuration
@Data
public class SecurityUserLoginHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler, AccessDeniedHandler {

	@Autowired
	private SecurityUrlInformation urlInformation;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		if(AjaxUtils.isAjax(request)) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("success", true);
			map.put("redirect", urlInformation.getMypage());

	        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
	        response.getWriter().write(JsonUtils.toJson(map));
	        response.getWriter().flush();
		} else {
			response.sendRedirect(urlInformation.getMypage());
		}
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		if(AjaxUtils.isAjax(request)) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("success", false);
			map.put("redirect", urlInformation.getLogin());
			map.put("error", exception.getMessage());

	        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
	        response.getWriter().write(JsonUtils.toJson(map));
	        response.getWriter().flush();
		} else {
			String query = String.format("?error=", URLEncoder.encode(exception.getMessage(), "utf-8"));
			response.sendRedirect(urlInformation.getLogin() + query);
		}
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
		if(AjaxUtils.isAjax(request)) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("success", false);
			map.put("redirect", urlInformation.getLogin());
			map.put("error", accessDeniedException.getMessage());

	        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
	        response.getWriter().write(JsonUtils.toJson(map));
	        response.getWriter().flush();
		} else {
			String query = String.format("?error=", URLEncoder.encode(accessDeniedException.getMessage(), "utf-8"));
			response.sendRedirect(urlInformation.getLogin() + query);
		}
	}

}
