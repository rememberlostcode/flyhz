
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

import com.flyhz.framework.auth.Authenticate;
import com.flyhz.framework.config.FinderConfig;

@Controller
@RequestMapping(value = "/")
public class LoginController {

	@Resource
	private Authenticate	auth;

	@Resource
	private FinderConfig		finderConfig;

	private String			webPageLogin;

	private String			webPageLogged;

	private String			webPageIndex;

	public String getWebPageIndex() {
		if (webPageIndex == null) {
			webPageIndex = UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/index";
			if (finderConfig.getConfig(FinderConfig.WEB_PAGE_INDEX) != null) {
				if (finderConfig.getConfig(FinderConfig.WEB_PAGE_INDEX).toString()
								.indexOf(FinderConfig.WEB_PAGE_INDEX) < 0) {
					webPageIndex = UrlBasedViewResolver.REDIRECT_URL_PREFIX
							+ (String) finderConfig.getConfig(FinderConfig.WEB_PAGE_INDEX);

				}
			}

		}
		return webPageIndex;
	}

	public String getWebPageLogged() {
		if (webPageLogged == null) {
			webPageLogged = UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/home";
			if (finderConfig.getConfig(FinderConfig.WEB_PAGE_LOGGED) != null) {
				if (finderConfig.getConfig(FinderConfig.WEB_PAGE_LOGGED).toString()
								.indexOf(FinderConfig.WEB_PAGE_LOGGED) < 0) {
					webPageLogged = UrlBasedViewResolver.REDIRECT_URL_PREFIX
							+ (String) finderConfig.getConfig(FinderConfig.WEB_PAGE_LOGGED);
				}
			}
		}

		return webPageLogged;
	}

	public String getWebPageLogin() {
		if (webPageLogin == null) {
			webPageLogin = UrlBasedViewResolver.FORWARD_URL_PREFIX + "/login";
			if (finderConfig.getConfig(FinderConfig.WEB_PAGE_LOGIN) != null) {
				if (finderConfig.getConfig(FinderConfig.WEB_PAGE_LOGIN).toString()
								.indexOf(FinderConfig.WEB_PAGE_LOGIN) < 0) {
					webPageLogin = UrlBasedViewResolver.FORWARD_URL_PREFIX
							+ (String) finderConfig.getConfig(FinderConfig.WEB_PAGE_LOGIN);
				}
			}
		}

		return webPageLogin;
	}

	@RequestMapping(value = "/loginAuth", method = RequestMethod.POST)
	public String Login(HttpServletRequest request, HttpServletResponse response, Model model)
			throws IOException {

		auth.recognize(request, response);

		return getWebPageLogged();
	}

	@RequestMapping(value = "/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		auth.removeMark(request, response);
		return getWebPageIndex();
	}
}
