
package com.flyhz.shop.web.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.flyhz.framework.auth.Authenticate;
import com.flyhz.framework.lang.ValidateException;
import com.flyhz.framework.lang.Protocol;
import com.flyhz.shop.dto.User;
import com.flyhz.shop.service.UserService;

@Controller
@RequestMapping(value = "/")
public class LoginController {

	@Resource
	private Authenticate	auth;

	@Resource
	private UserService		userService;

	@RequestMapping(value = "/loginAuth", method = RequestMethod.POST)
	public String Login(String username, String password, String verifycode,
			HttpServletRequest request, HttpServletResponse response, Model model)
			throws IOException {
		User user;
		try {
			user = userService.login(username, password, verifycode);
			if (user != null) {
				auth.mark(user.getId(), request, response);
			}
		} catch (ValidateException e) {
			Protocol protocol = new Protocol();
			protocol.setCode(e.getCode());
		}

		return "";
	}

	@RequestMapping(value = "/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		auth.removeMark(request, response);
		return "";
	}
}
