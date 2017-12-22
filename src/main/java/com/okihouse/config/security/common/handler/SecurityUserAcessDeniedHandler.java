package com.okihouse.config.security.common.handler;

import com.okihouse.config.security.common.data.SecurityUrlData;
import com.okihouse.util.AjaxUtils;
import com.okihouse.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by okihouse16 on 2017. 12. 21..
 */
@Configuration
public class SecurityUserAcessDeniedHandler implements AccessDeniedHandler {

    @Autowired
    private SecurityUrlData securityUrlData;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        if (AjaxUtils.isAjax(request) || AjaxUtils.isApi(request)) {
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("error", accessDeniedException.getMessage());

            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            response.getWriter().write(JsonUtils.toJson(map));
            response.getWriter().flush();
        } else {
            response.sendRedirect(securityUrlData.getAccessdenied());
        }
    }

}
