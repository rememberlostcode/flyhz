
package com.flyhz.shop.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 首页Controller
 * 
 * @author fuwb 20140326
 */
@Controller
@RequestMapping(value = "/")
public class IndexController {
	@RequestMapping(value = { "index", "" })
	public String index(Model model) {
		model.addAttribute("hello", "Hello Smile SApp!");
		return "index";
	}

	@RequestMapping(value = { "register" })
	public String register(Model model) {
		model.addAttribute("hello", "Hello Smile SApp!");
		return "register2";
	}

	@RequestMapping(value = { "search" })
	public String search(Model model) {
		model.addAttribute("search", "Search Smile SApp!");
		return "index/search";
	}
}
