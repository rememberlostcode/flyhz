
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

	// http://localhost:8088/smile/loginAuth.json?username=admin&password=123456
	@RequestMapping(value = "loginAuth", method = RequestMethod.GET)
	public String login(String username, String password, String verifycode,
			HttpServletRequest request, HttpServletResponse response, Model model)
			throws IOException {
		Protocol protocol = new Protocol();
		try {
			UserDto user = userService.login(username, password, verifycode);
			if (user != null) {
				auth.mark(user.getId(), request, response);
				protocol.setCode(200000);
				protocol.setData(user);
			} else {
				protocol.setCode(101026);
			}
		} catch (ValidateException e) {
			protocol.setCode(e.getCode());
		}
		model.addAttribute("protocol", protocol);
		return "";
	}

	// http://localhost:8088/smile/loginAuto.json?username=admin&token=
	@RequestMapping(value = "loginAuto", method = RequestMethod.GET)
	public String loginAuto(String username, String token, String verifycode,
			HttpServletRequest request, HttpServletResponse response, Model model)
			throws IOException {
		Protocol protocol = new Protocol();
		try {
			UserDto user = userService.loginAuto(username, token, verifycode);
			if (user != null) {
				auth.mark(user.getId(), request, response);
				protocol.setCode(200000);
				protocol.setData(user);
			} else {
				protocol.setCode(101026);
			}
		} catch (ValidateException e) {
			protocol.setCode(e.getCode());
		}
		model.addAttribute("protocol", protocol);
		return "";
	}

	// http://localhost:8088/smile/logout.json?username=admin&token=
	@RequestMapping(value = "logout", method = RequestMethod.GET)
	public String logout(String username, String token, String verifycode,
			HttpServletRequest request, HttpServletResponse response, Model model) {
		Protocol protocol = new Protocol();
		try {
			UserDto user = userService.logout(username, token, verifycode);
			if (user != null) {
				auth.removeMark(request, response);
				protocol.setCode(0);
				protocol.setData(user);
			} else {
				protocol.setCode(1);
			}
		} catch (ValidateException e) {
			protocol.setCode(e.getCode());
		}
		model.addAttribute("protocol", protocol);
		return "";
	}
}
