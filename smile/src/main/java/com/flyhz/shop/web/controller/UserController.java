
package com.flyhz.shop.web.controller;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.shop.dto.UserDetailDto;
import com.flyhz.shop.dto.UserDto;
import com.flyhz.shop.service.UserService;

/**
 * 
 * 类说明：用户管理
 * 
 * @author robin 2014-4-1下午12:11:14
 * 
 */
@Controller
@RequestMapping(value = "/")
public class UserController {
	protected Logger	log	= LoggerFactory.getLogger(UserController.class);

	@Resource
	private UserService	userService;

	@RequestMapping(value = { "register" })
	public String register(Model model) {
		model.addAttribute("hello", "Hello Smile SApp!");
		return "register";
	}

	@RequestMapping(value = { "saveRegister" }, method = RequestMethod.POST)
	@ResponseBody
	public String saveRegister(Model model, @ModelAttribute UserDetailDto userDetail) {
		Integer code = 0;
		if (userDetail != null) {
			if (StringUtils.isNotBlank(userDetail.getUsername())
					&& StringUtils.isNotBlank(userDetail.getPassword())) {
				try {
					UserDto user = userService.register(userDetail);
					code = 1;
				} catch (ValidateException e) {
					code = 4;
					log.error("=======在注册时=========" + e.getMessage());
				}
			} else {
				code = 3;// 用户名或者密码错误
			}
		} else {
			code = 2;
		}
		model.addAttribute("data", code);
		return null;
	}
}
