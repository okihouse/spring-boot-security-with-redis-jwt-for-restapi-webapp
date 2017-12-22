package com.okihouse.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

public class AjaxUtils {

	public static boolean isAjax(HttpServletRequest request) {
		String accept = request.getHeader("accept");
		String ajax = request.getHeader("X-Requested-With");
		return (StringUtils.indexOf(accept, "json") > -1 && StringUtils.isNotEmpty(ajax));
	}

	public static boolean isApi(HttpServletRequest request) {
		String accept = request.getHeader("Content-Type");
        String ajax = request.getHeader("X-Requested-With");
        return (StringUtils.indexOf(accept, "json") > -1 && StringUtils.isEmpty(ajax));
	}

}
