
package com.flyhz.shop.web.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.flyhz.framework.auth.Identify;
import com.flyhz.framework.lang.Protocol;
import com.flyhz.framework.lang.ValidateException;
import com.flyhz.framework.util.DateUtil;
import com.flyhz.shop.dto.OrderPayDto;
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
			protocol.setData(orderService.listOrders(16, status));
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

	@RequestMapping(value = "/status")
	public void status(@Identify Integer userId, @RequestParam String num, Model model) {
		Protocol protocol = new Protocol();
		Integer code = 200000;
		try {
			if (userId == null) {
				code = 100000;
			} else {
				OrderPayDto orderPayDto = new OrderPayDto();
				orderPayDto.setUserId(userId);
				orderPayDto.setNumber(num);
				OrderPayDto order = orderService.getOrderPay(orderPayDto);
				if (order != null) {
					order.setTime(DateUtil.dateToStr(order.getGmtCreate()));
					protocol.setData(order);
				} else {
					code = 500000;
				}
			}
		} catch (Exception e) {
			code = 400000;
		}
		protocol.setCode(code);
		model.addAttribute("protocol", protocol);
	}
}
