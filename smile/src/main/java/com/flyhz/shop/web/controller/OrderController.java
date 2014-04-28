
package com.flyhz.shop.web.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.flyhz.framework.auth.Identify;
import com.flyhz.framework.lang.Protocol;
import com.flyhz.framework.lang.ValidateException;
import com.flyhz.shop.service.OrderService;

/**
 * 订单Controller
 * 
 * @author zhangb 2014年4月24日 下午1:46:05
 * 
 */
@Controller
@RequestMapping(value = "/order")
public class OrderController {
	@Resource
	private OrderService	orderService;

	@RequestMapping(value = "/list")
	public void list(@Identify Integer userId, String status, Model model) {
		Protocol protocol = new Protocol();
		try {
			protocol.setData(orderService.listOrders(userId, status));
			protocol.setCode(200000);
		} catch (ValidateException e) {
			protocol.setCode(e.getCode());
		}
		model.addAttribute("protocol", protocol);
	}

	@RequestMapping(value = "/close")
	public void close(@Identify Integer userId, Integer id, Model model) {
		Protocol protocol = new Protocol();
		try {
			orderService.closeOrder(userId, id);
			protocol.setCode(200000);
		} catch (ValidateException e) {
			protocol.setCode(e.getCode());
		}
		model.addAttribute("protocol", protocol);
	}
}
