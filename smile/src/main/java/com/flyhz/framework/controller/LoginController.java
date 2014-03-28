
package com.flyhz.framework.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import com.flyhz.framework.config.WebConfigurer;
import com.flyhz.framework.lang.Authenticate;
import com.flyhz.framework.lang.BusinessException;
import com.flyhz.framework.lang.Config;

@Controller
@RequestMapping(value = "/")
public class LoginController {

	@Resource
	private Authenticate	auth;

	@Resource
	private Config			webConfig;

	private String			webPageLogin;

	private String			webPageLogged;

	private String			webPageIndex;

	public String getWebPageIndex() {
		if (webPageIndex == null) {
			webPageIndex = UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/index";
			if (webConfig.getConfig(WebConfigurer.WEB_PAGE_INDEX) != null) {
				if (webConfig.getConfig(WebConfigurer.WEB_PAGE_INDEX).toString()
								.indexOf(WebConfigurer.WEB_PAGE_INDEX) < 0) {
					webPageIndex = UrlBasedViewResolver.REDIRECT_URL_PREFIX
							+ (String) webConfig.getConfig(WebConfigurer.WEB_PAGE_INDEX);

				}
			}

		}
		return webPageIndex;
	}

	public String getWebPageLogged() {
		if (webPageLogged == null) {
			webPageLogged = UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/home";
			if (webConfig.getConfig(WebConfigurer.WEB_PAGE_LOGGED) != null) {
				if (webConfig.getConfig(WebConfigurer.WEB_PAGE_LOGGED).toString()
								.indexOf(WebConfigurer.WEB_PAGE_LOGGED) < 0) {
					webPageLogged = UrlBasedViewResolver.REDIRECT_URL_PREFIX
							+ (String) webConfig.getConfig(WebConfigurer.WEB_PAGE_LOGGED);
				}
			}
		}

		return webPageLogged;
	}

	public String getWebPageLogin() {
		if (webPageLogin == null) {
			webPageLogin = UrlBasedViewResolver.FORWARD_URL_PREFIX + "/login";
			if (webConfig.getConfig(WebConfigurer.WEB_PAGE_LOGIN) != null) {
				if (webConfig.getConfig(WebConfigurer.WEB_PAGE_LOGIN).toString()
								.indexOf(WebConfigurer.WEB_PAGE_LOGIN) < 0) {
					webPageLogin = UrlBasedViewResolver.FORWARD_URL_PREFIX
							+ (String) webConfig.getConfig(WebConfigurer.WEB_PAGE_LOGIN);
				}
			}
		}

		return webPageLogin;
	}

	@RequestMapping(value = "/loginAuth", method = RequestMethod.POST)
	public String Login(HttpServletRequest request, HttpServletResponse response, Model model)
			throws IOException {
		try {
			auth.login(request, response);
		} catch (BusinessException e) {
			model.addAttribute("error", e.getMessage());
			return getWebPageLogin();
		}
		return getWebPageLogged();
	}

	@RequestMapping(value = "/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		auth.logout(request, response);
		return getWebPageIndex();
	}
}
