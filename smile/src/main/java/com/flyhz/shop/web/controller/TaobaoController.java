
package com.flyhz.shop.web.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.flyhz.framework.auth.Identify;
import com.flyhz.framework.lang.Protocol;
import com.flyhz.framework.lang.TaobaoData;
import com.flyhz.framework.lang.ValidateException;
import com.flyhz.shop.dto.OrderPayDto;
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
	public void paymentStatus(@Identify Integer userId,Model model, String numbers, Long tid) {
		String result = "0";
		
		Protocol protocol = new Protocol();
		Integer code = 200000;
		try {
			if (userId == null) {
				code = 100000;
			} else {
				result = orderService.getOrderPayStatusByTid(numbers, tid);
				OrderPayDto orderPayDto = new OrderPayDto();
				orderPayDto.setStatus(result);
				protocol.setData(orderPayDto);
			}
		} catch (ValidateException e) {
			code = e.getCode();
		} catch (Exception e) {
			code = 400000;
		}
		protocol.setCode(code);
		model.addAttribute("protocol", protocol);
	}
	
	@RequestMapping(value = "/startMessage")
	public void startMessage(Model model, HttpServletResponse response) {
		try {
			taobaoData.startMessageHandler();
			PrintWriter writer = response.getWriter();
			response.reset();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html");
			writer.println("操作成功!");
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/stopMessage")
	public void stopMessage(Model model, HttpServletResponse response) {
		try {
			taobaoData.stopMessageHandler();
			PrintWriter writer = response.getWriter();
			response.reset();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html");
			writer.println("操作成功!");
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
