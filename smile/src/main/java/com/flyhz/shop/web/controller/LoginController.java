
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
import com.flyhz.framework.lang.Protocol;
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

	@RequestMapping(value = "/loginAuth", method = RequestMethod.GET)
	public String Login(String username, String password, String verifycode,
			HttpServletRequest request, HttpServletResponse response, Model model)
			throws IOException {
		Protocol protocol = new Protocol();
		UserDto user;
		try {
			user = userService.login(username, password, verifycode);
			if (user != null) {
				auth.mark(user.getId(), request, response);
				protocol.setCode(0);
				protocol.setData("login success");
			} else {
				protocol.setCode(1);
				protocol.setData("login fail");
			}
		} catch (ValidateException e) {
			protocol.setCode(e.getCode());
		}
		model.addAttribute("protocol", protocol);
		return "";
	}

	@RequestMapping(value = "/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		auth.removeMark(request, response);
		return "";
	}
}