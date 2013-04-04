
package com.flyhz.shop.web.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.flyhz.framework.auth.Identify;
import com.flyhz.framework.lang.Protocol;
import com.flyhz.framework.lang.ValidateException;
import com.flyhz.shop.persistence.entity.ConsigneeModel;
import com.flyhz.shop.service.UserService;

/**
 * 收货人地址 Controller
 * 
 * @author fuwb 20140402
 */
@Controller
@RequestMapping(value = "/consignee")
public class ConsigneeController {
	@Resource
	private UserService	userService;

	// @RequestMapping(value = "/setIdCard", method = RequestMethod.POST)
	@RequestMapping(value = "/setIdCard")
	public void setIdCard(@Identify Integer userId, @RequestParam(value = "conid") Integer conid,
			MultipartFile multipartFile, Model model) {
		Protocol protocol = new Protocol();
		try {
			userService.setIdCardImg(userId, conid, multipartFile);
			protocol.setCode(200000);
		} catch (ValidateException e) {
			protocol.setCode(e.getCode());
		}
		model.addAttribute("protocol", protocol);
	}

	// @RequestMapping(value = "/add", method = RequestMethod.POST)
	@RequestMapping(value = "/add")
	public void addConsignee(@Identify Integer userId, ConsigneeModel consignee, Model model) {
		Protocol protocol = new Protocol();
		try {
			consignee.setUserId(userId);
			userService.addConsignee(consignee);
			protocol.setCode(200000);
		} catch (ValidateException e) {
			protocol.setCode(e.getCode());
		}
		model.addAttribute("protocol", protocol);
	}

	// @RequestMapping(value = "/modify", method = RequestMethod.POST)
	@RequestMapping(value = "/modify")
	public void modifyConsignee(@Identify Integer userId, ConsigneeModel consignee, Model model) {
		Protocol protocol = new Protocol();
		try {
			consignee.setUserId(userId);
			userService.modifyConsignee(consignee);
			protocol.setCode(200000);
		} catch (ValidateException e) {
			protocol.setCode(e.getCode());
		}
		model.addAttribute("protocol", protocol);
	}

	// @RequestMapping(value = "/remove", method = RequestMethod.POST)
	@RequestMapping(value = "/remove")
	public void removeConsignee(@Identify Integer userId,
			@RequestParam(value = "conid") Integer conid, Model model) {
		Protocol protocol = new Protocol();
		try {
			userService.removeConsignee(userId, conid);
			protocol.setCode(200000);
		} catch (ValidateException e) {
			protocol.setCode(e.getCode());
		}
		model.addAttribute("protocol", protocol);
	}
}
