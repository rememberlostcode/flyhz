
package com.flyhz.shop.web.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.flyhz.framework.lang.RedisRepository;
import com.flyhz.framework.lang.SolrData;
import com.flyhz.framework.lang.TaobaoData;
import com.flyhz.shop.service.BuildService;

@Controller
@RequestMapping(value = "/build")
public class BuildController {

	@Resource
	private BuildService	buildService;
	@Resource
	private RedisRepository	redisRepository;
	@Resource
	@Value(value = "${smile.taobao.url}")
	private String			smile_taobao_url;
	@Resource
	private SolrData		solrData;
	@Resource
	private TaobaoData		taobaoData;

	/**
	 * build 所有
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/all")
	public String all(Model model) {
		buildService.buildData();
		return "build";
	}

	/**
	 * build solr
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/solr")
	public String solr(Model model) {
		buildService.buildSolr();
		model.addAttribute("result", "solr finsh!");
		return "build";
	}

	/**
	 * build redis
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/redis")
	public String redis(Model model) {
		buildService.buildRedis();
		model.addAttribute("result", "redis finsh!");
		return "build";
	}

	@RequestMapping(value = "/test")
	public String test(Model model) {

		model.addAttribute("result", "美元汇率=" + solrData.getDollarExchangeRate());

		return "build";
	}

	/**
	 * 获取淘宝店铺地址
	 * 
	 * @param model
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/url")
	public void url(Model model, HttpServletRequest request, HttpServletResponse response) {
		try {
			PrintWriter writer = response.getWriter();
			response.reset();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html");
			writer.println(smile_taobao_url);
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 清除solr中商品的数据
	 * 
	 * @param model
	 * @param response
	 */
	@RequestMapping(value = "/clean")
	public void clean(Model model, HttpServletResponse response) {
		try {
			solrData.cleanProduct();
			PrintWriter writer = response.getWriter();
			response.reset();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html");
			writer.println("solr已经清空商品!");
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/startMessage")
	public void startMessage(Model model, HttpServletResponse response) {
		try {
			taobaoData.startMessageHandler();
			PrintWriter writer = response.getWriter();
			response.reset();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html");
			writer.println("淘宝消息进程已经开启!");
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
