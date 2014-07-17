
package com.flyhz.shop.web.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import com.flyhz.framework.auth.Authenticate;
import com.flyhz.framework.lang.ValidateException;
import com.flyhz.shop.dto.UserDto;
import com.flyhz.shop.service.UserService;

@Controller
@RequestMapping(value = "/")
public class LoginController {

	@Resource
	private Authenticate	auth;

	@Resource
	private UserService		userService;

	@RequestMapping(value = "loginAuth", method = RequestMethod.POST)
	public String login(String username, String password, String verifycode,
			HttpServletRequest request, HttpServletResponse response, Model model)
			throws IOException {
		try {
			UserDto user = userService.login(username, password, verifycode);
			if (user != null) {
				auth.mark(user.getId(), request, response);
				model.addAttribute("message", "登陆成功");
				// 设置用户名
				HttpSession session = request.getSession();
				session.setAttribute("loginName", username);
			} else {
				model.addAttribute("message", "登陆失败");
			}
		} catch (ValidateException e) {
			model.addAttribute("message", "登陆异常");
		}
		model.addAttribute("current", "0");
		
		Cookie cookie = new Cookie("JSESSIONID", request.getSession().getId());
		cookie.setPath("/");
		response.addCookie(cookie);
		return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/index";
	}

	@RequestMapping(value = "logout", method = RequestMethod.POST)
	public String logout(String username, String token, String verifycode,
			HttpServletRequest request, HttpServletResponse response, Model model) {
		try {
			UserDto user = userService.logout(username, token, verifycode);
			if (user != null) {
				auth.removeMark(request, response);
				model.addAttribute("message", "注销成功");
				// 设置用户名
				HttpSession session = request.getSession();
				session.setAttribute("loginName", null);
			} else {
				model.addAttribute("message", "注销成功");
			}
		} catch (ValidateException e) {
			model.addAttribute("message", "注销异常");
		}
		model.addAttribute("current", "0");
		return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/index";
	}
}
