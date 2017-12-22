package com.okihouse.config.security.common.entrypoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Service;

import com.okihouse.config.security.common.data.SecurityUrlData;
import com.okihouse.util.AjaxUtils;
import com.okihouse.util.JsonUtils;

@Service
public class SecurityUserAccessEntryPoint implements AuthenticationEntryPoint {

	@Autowired
	private SecurityUrlData urlInformation;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
			throws IOException, ServletException {
        if(AjaxUtils.isAjax(request) || AjaxUtils.isApi(request)) { // TODO: Updated api request header
			Map<String, Object> map = new HashMap<>();
			map.put("success", false);
			map.put("redirect", urlInformation.getLogin());

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
	        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
	        response.getWriter().write(JsonUtils.toJson(map));
	        response.getWriter().flush();
	        response.getWriter().close();
		} else {
			response.sendRedirect(request.getContextPath() + urlInformation.getLogin());
		}
	}
}
