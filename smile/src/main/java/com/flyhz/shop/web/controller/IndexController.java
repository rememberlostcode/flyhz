
package com.flyhz.shop.web.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.shop.persistence.entity.VersionModel;
import com.flyhz.shop.service.VersionService;

/**
 * 首页Controller
 * 
 * @author fuwb 20140326
 */
@Controller
@RequestMapping(value = "/")
public class IndexController {
	@Resource
	private VersionService versionService;
	
	@RequestMapping(value = { "index", "" })
	public String index(Model model) {
		model.addAttribute("hello", "Hello Smile SApp!");
		return "index";
	}

	@RequestMapping(value = { "search" })
	public String search(Model model) {
		model.addAttribute("search", "Search Smile SApp!");
		return "index/search";
	}
	
	@RequestMapping(value = { "version" })
	public String version(Model model) {
		model.addAttribute("versionModel",versionService.getLastestModel());
		return "index/version";
	}
	@RequestMapping(value = { "versionUpdate" })
	public String versionUpdate(Model model,VersionModel versionModel,MultipartFile file) {
		Integer code = 200000;
		String message = "";
		try {
			versionService.insertVersion(versionModel,file);
		} catch (ValidateException e) {
			e.printStackTrace();
			code = e.getCode();
			message = e.getMessage();
		}
		
		if(code.equals(200000)){
			model.addAttribute("result",code);
		} else {
			model.addAttribute("result",code + (message!=null?message:""));
		}
		return "index/version";
	}
}
