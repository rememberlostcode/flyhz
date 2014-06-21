
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
	@Resource
	@Value(value = "${smile.taobao.url}")
	private String			smile_taobao_url;
	@Resource
	private SolrData		solrData;

	@RequestMapping(value = "/all")
	public String all(Model model) {
		buildService.buildData();
		return "build";
	}

	@RequestMapping(value = "/solr")
	public String solr(Model model) {
		buildService.buildSolr();
		model.addAttribute("result", "solr finsh!");
		return "build";
	}

	@RequestMapping(value = "/redis")
	public String redis(Model model) {
		buildService.buildRedis();
		model.addAttribute("result", "redis finsh!");
		return "build";
	}

	@RequestMapping(value = "/test")
	public String test(Model model) {
		try {
			model.addAttribute("result",
					JSONUtil.getEntity2Json(redisRepository.getProductFromRedis("50")));

		} catch (ValidateException e) {
			e.printStackTrace();
		}
		return "build";
	}

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
}
