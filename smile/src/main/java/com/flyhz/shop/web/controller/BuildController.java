
package com.flyhz.shop.web.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.flyhz.framework.lang.RedisRepository;
import com.flyhz.framework.lang.ValidateException;
import com.flyhz.framework.util.JSONUtil;
import com.flyhz.shop.service.BuildService;

@Controller
@RequestMapping(value = "/build")
public class BuildController {

	@Resource
	private BuildService	buildService;
	@Resource
	private RedisRepository	redisRepository;

	@RequestMapping(value = "/all")
	public String all(Model model) {
		buildService.buildData();
		return "build";
	}

	@RequestMapping(value = "/solr")
	public String solr(Model model) {
		buildService.buildSolr();
		return "build";
	}

	@RequestMapping(value = "/redis")
	public String redis(Model model) {
		buildService.buildRedis();
		return "build";
	}

	@RequestMapping(value = "/test")
	public String test(Model model) {
		try {
			model.addAttribute("result",
					JSONUtil.getEntity2Json(redisRepository.getProductFromRedis("50")));
			String orderJson = redisRepository.getOrderFromRedis(1, 18);
			System.out.println(orderJson);

			// redisRepository.buildOrderToRedis(1, 1001,
			// "测试订单buildOrderToRedis");
			// orderJson = redisRepository.getOrderFromRedis(1001, 1);
			// System.out.println(orderJson);

			// redisRepository.reBuildOrderToRedis(1, 1);

			// buildService.getDollarExchangeRate();

		} catch (ValidateException e) {
			e.printStackTrace();
		}
		return "build";
	}
	
	@RequestMapping(value = "/url", method = RequestMethod.POST)
	public void url(Model model,HttpServletRequest request,
			HttpServletResponse response) {
		try {
			PrintWriter writer = response.getWriter();
			response.reset();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html");
			writer.println("http://h5.m.taobao.com/awp/core/detail.htm?id=38752474914");
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
