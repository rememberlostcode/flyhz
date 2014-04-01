
package com.flyhz.shop.web.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.flyhz.shop.service.BuildService;

@Controller
@RequestMapping(value = "/")
public class BuildController {

	@Resource
	private BuildService	buildService;

	@RequestMapping(value = "/build")
	public String build(Model model) {
		buildService.buildData();
		return "build";
	}
}
