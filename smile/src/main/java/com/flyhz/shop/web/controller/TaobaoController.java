
package com.flyhz.shop.web.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.flyhz.framework.lang.TaobaoData;
import com.flyhz.shop.service.OrderService;

/**
 * 淘宝Controller
 * 
 * @author zhangb
 * 
 */
@Controller
@RequestMapping(value = "/taobao")
public class TaobaoController {
	@Resource
	private TaobaoData		taobaoData;
	@Resource
	private OrderService	orderService;

	@RequestMapping(value = "/syn")
	public void syn(Model model, HttpServletResponse response) {
		String result = "同步物流完成！";
		try {
			taobaoData.synchronizationLogistics();
		} catch (Exception e) {
			e.printStackTrace();
			result = "同步物流失败！！！";
		}
		try {
			PrintWriter writer = response.getWriter();
			response.reset();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html");
			writer.println(result);
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/paymentStatus")
	public void paymentStatus(Model model, Integer orderId, Long tid, HttpServletResponse response) {
		String result = "0";
		try {
			result = orderService.getOrderPayStatusByTid(orderId, tid);
		} catch (Exception e) {
			e.printStackTrace();
			result = "获取淘宝订单付款状态失败！！！";
		}
		try {
			PrintWriter writer = response.getWriter();
			response.reset();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html");
			writer.println(result);
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
