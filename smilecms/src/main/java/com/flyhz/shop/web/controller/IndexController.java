
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
	@RequestMapping(value = { "home", "index", "" })
	public String index(Model model) {
		model.addAttribute("current", "0");
		return "/index/index";
	}
}
