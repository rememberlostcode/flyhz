
package com.flyhz.framework.lang;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.flyhz.framework.FinderConfig;
import com.flyhz.framework.auth.Authenticate;

public class AccessInterceptor extends HandlerInterceptorAdapter implements InitializingBean {

	protected Logger		log	= LoggerFactory.getLogger(getClass());

	@Resource
	private Authenticate	auth;

	@Resource
	private FinderConfig	config;

	private String			webPageLoginIndex;

	@Override
	public void afterPropertiesSet() throws Exception {
		webPageLoginIndex = "/login";
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
		Integer id = auth.recognize(request, response);
		if (id == null) {
			response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
			response.sendRedirect(webPageLoginIndex);
			return false;
		} else {
			return true;
		}

	}
}