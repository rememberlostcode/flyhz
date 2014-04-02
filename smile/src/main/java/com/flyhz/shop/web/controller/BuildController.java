
package com.flyhz.shop.web.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.flyhz.shop.service.BuildService;
import com.flyhz.shop.service.ProductService;

@Controller
@RequestMapping(value = "/build")
public class BuildController {

	@Resource
	private BuildService	buildService;
	@Resource
	private ProductService	productService;

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
		System.out.println(productService.getProductFromRedis("50"));
		return "build";
	}
}
