package com.example.config.security.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Service;

import com.example.config.security.url.SecurityUrlInformation;
import com.example.util.AjaxUtils;
import com.example.util.JsonUtils;

@Service
public class SecurityUserAccessHandler implements AuthenticationEntryPoint {

	@Autowired
	private SecurityUrlInformation urlInformation;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
			throws IOException, ServletException {

		if(AjaxUtils.isAjax(request)) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("success", false);
			map.put("redirect", urlInformation.getLogin());

	        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
	        response.getWriter().write(JsonUtils.toJson(map));
	        response.getWriter().flush();
	        response.getWriter().close();
		} else {
			response.sendRedirect(request.getContextPath() + urlInformation.getLogin());
		}
	}
}
