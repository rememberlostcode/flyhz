
package com.flyhz.shop.web.controller;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.flyhz.framework.auth.Identify;
import com.flyhz.framework.lang.Protocol;
import com.flyhz.framework.lang.ValidateException;
import com.flyhz.framework.util.JSONUtil;
import com.flyhz.shop.dto.ConsigneeDetailDto;
import com.flyhz.shop.dto.UserDetailDto;
import com.flyhz.shop.dto.UserDto;
import com.flyhz.shop.service.OrderService;
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
	protected Logger		log	= LoggerFactory.getLogger(UserController.class);

	@Resource
	private UserService		userService;
	@Resource
	private OrderService	orderService;

	@RequestMapping(value = { "register" })
	public String register(Model model) {
		return "register";
	}

	@RequestMapping(value = { "saveRegister" }, method = RequestMethod.POST)
	public String saveRegister(Model model, @ModelAttribute UserDetailDto userDetail) {
		Protocol protocol = new Protocol();
		Integer code = 0;
		String msg = "";
		if (userDetail != null) {
			if (StringUtils.isNotBlank(userDetail.getUsername())
					&& StringUtils.isNotBlank(userDetail.getPassword())) {
				try {
					UserDto user = userService.register(userDetail);
					code = 1;
				} catch (ValidateException e) {
					code = 4;
					msg = e.getMessage();
					log.error("=======在注册时=========" + e.getMessage());
				}
			} else {
				code = 3;// 用户名或者密码错误
			}
		} else {
			code = 2;
		}
		protocol.setCode(code);
		protocol.setData(msg);
		model.addAttribute("protocol", protocol);
		return "";
	}

	@RequestMapping(value = { "user/consignees" })
	public String getConsinee(Model model) {
		Protocol protocol = new Protocol();
		Integer code = 0;
		List<ConsigneeDetailDto> consigneeDtoList = null;
		try {
			consigneeDtoList = userService.listConsignees(1);
		} catch (Exception e) {
			code = 4;
			e.printStackTrace();
			log.error("=======查询地址时=========" + e.getMessage());
		}
		protocol.setCode(code);
		protocol.setData(JSONUtil.getEntity2Json(consigneeDtoList));
		model.addAttribute("protocol", protocol);
		return "";
	}

	/**
	 * 订单确认信息,这时还没有生成订单
	 * 
	 * @param model
	 * @param orderDto
	 * @return
	 */
	@RequestMapping(value = { "user/orderInform" })
	public String orderInform(Model model, @RequestParam String[] pids, @RequestParam Integer cid) {
		Protocol protocol = new Protocol();
		Integer code = 0;
		String details = "";
		if ((pids == null || pids.length == 0) || cid == null)
			code = 5;

		try {
			details = orderService.generateOrder(1, cid, pids, false);
			code = 1;
		} catch (ValidateException e) {
			code = 4;
			log.error("=======在生成订单时=========" + e.getMessage());
		}
		protocol.setCode(code);
		protocol.setData(details);
		model.addAttribute("protocol", protocol);
		return "";
	}

	/**
	 * 确认订单信息并生成订单
	 * 
	 * @param model
	 * @param orderDto
	 * @return
	 */
	@RequestMapping(value = { "user/confirmOrder" })
	public String confirmOrder(Model model, @RequestParam String[] pids, @RequestParam Integer cid) {
		Protocol protocol = new Protocol();
		Integer code = 0;
		String details = "";
		if ((pids == null || pids.length == 0) || cid == null)
			code = 5;

		try {
			details = orderService.generateOrder(1, cid, pids, true);
			code = 1;
		} catch (ValidateException e) {
			code = 4;
			log.error("=======在生成订单时=========" + e.getMessage());
		}
		protocol.setCode(code);
		protocol.setData(details);
		model.addAttribute("protocol", protocol);
		return "";
	}

	/**
	 * 设置用户信息，包括邮箱、密码和手机号
	 * 
	 * @param userId
	 * @param field
	 * @param fval
	 * @param model
	 */
	@RequestMapping(value = "user/setInfo")
	public void setInfo(@Identify Integer userId, @RequestParam(value = "field") String field,
			@RequestParam(value = "fval") Object fval, Model model) {
		Protocol protocol = new Protocol();
		try {
			userService.setPersonalInformation(userId, field, fval);
			protocol.setCode(200000);
		} catch (ValidateException e) {
			protocol.setCode(e.getCode());
		}
		model.addAttribute("protocol", protocol);
	}

	/**
	 * 解除绑定邮箱
	 * 
	 * @param userId
	 * @param model
	 */
	@RequestMapping(value = "user/releiveEmail")
	public void releiveEmail(@Identify Integer userId, Model model) {
		Protocol protocol = new Protocol();
		try {
			userService.relieveEmail(userId);
			protocol.setCode(200000);
		} catch (ValidateException e) {
			protocol.setCode(e.getCode());
		}
		model.addAttribute("protocol", protocol);
	}
}
