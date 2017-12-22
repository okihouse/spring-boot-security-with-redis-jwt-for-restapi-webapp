package com.okihouse.config.security.web.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Service;

import com.okihouse.config.security.common.data.SecurityUrlData;
import com.okihouse.util.AjaxUtils;
import com.okihouse.util.JsonUtils;

@Service
public class WebSecurityUserLogoutHandler extends AbstractAuthenticationTargetUrlRequestHandler implements LogoutSuccessHandler {

	@Autowired
	private SecurityUrlData urlInformation;

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		if(AjaxUtils.isAjax(request)) {
			Map<String, Object> map = new HashMap<>();
			map.put("success", true);
			map.put("redirect", urlInformation.getLogin());

	        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
	        response.getWriter().write(JsonUtils.toJson(map));
	        response.getWriter().flush();
	        response.getWriter().close();
		} else {
			super.handle(request, response, authentication);
		}
	}

}
