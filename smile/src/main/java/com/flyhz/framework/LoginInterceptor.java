
package com.flyhz.framework;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.flyhz.framework.config.WebConfigurer;
import com.flyhz.framework.id.WebUser;
import com.flyhz.framework.lang.Authenticate;
import com.flyhz.framework.lang.Config;

public class LoginInterceptor extends HandlerInterceptorAdapter implements InitializingBean {

	protected Logger		log	= LoggerFactory.getLogger(getClass());

	@Resource
	private Authenticate	auth;

	@Resource
	private Config			webConfig;

	private String			webPageLoginIndex;

	@Override
	public void afterPropertiesSet() throws Exception {
		webPageLoginIndex = (webConfig.getConfig(WebConfigurer.WEB_PAGE_LOGIN) != null ? (String) webConfig.getConfig(WebConfigurer.WEB_PAGE_LOGIN)
				: "/login");
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
		WebUser webUser = auth.identify(request, response);
		if (webUser == null) {
			response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
			response.sendRedirect(webPageLoginIndex);
			return false;
		} else {
			return true;
		}

	}
}