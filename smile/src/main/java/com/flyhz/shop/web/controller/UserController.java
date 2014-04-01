
package com.flyhz.shop.web.controller;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.flyhz.shop.dto.UserDetailDto;

/**
 * 
 * 类说明：用户管理
 * 
 * @author robin 2014-4-1下午12:11:14
 * 
 */
@Controller
@RequestMapping(value = "/user")
public class UserController {
	protected Logger	log	= LoggerFactory.getLogger(UserController.class);

	@RequestMapping(value = { "register" })
	public String register(Model model) {
		model.addAttribute("hello", "Hello Smile SApp!");
		return "register";
	}

	@RequestMapping(value = { "saveRegister" })
	public String saveRegister(Model model, @ModelAttribute UserDetailDto userDetail) {
		Integer code = 0;
		if (userDetail != null) {
			if (StringUtils.isNotBlank(userDetail.getUsername())
					&& StringUtils.isNotBlank(userDetail.getPassword())) {
				code = 1;
			} else {
				code = 3;// 用户名或者密码错误
			}
		} else {
			code = 2;
		}
		model.addAttribute("code", code);
		return null;
	}
}
