
package com.flyhz.shop.web.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.flyhz.framework.auth.Identify;
import com.flyhz.framework.lang.TaobaoData;
import com.flyhz.framework.lang.ValidateException;
import com.flyhz.framework.util.TaobaoTokenUtil;
import com.flyhz.shop.dto.TaobaoParameters;
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
	private Logger			log	= LoggerFactory.getLogger(IndexController.class);
	@Resource
	private VersionService	versionService;
	@Resource
	private TaobaoData taobaoData;

	@RequestMapping(value = { "index", "" })
	public String index(Model model) {
		return "index";
	}
	
	@RequestMapping(value = { "taobaoapp", "" })
	public String taobaoapp(Model model, TaobaoParameters taobaoParameters) {
		if (taobaoParameters != null) {
			log.info("top_appkey=" + taobaoParameters.getTop_appkey());
			log.info("top_session=" + taobaoParameters.getTop_session());
			
			TaobaoTokenUtil.setAccessToken(taobaoParameters.getTop_session());
			TaobaoTokenUtil.writeToken();
			
			taobaoData.stopMessageHandler();
			taobaoData.startMessageHandler();
		}
		return "index";
	}
	
	@RequestMapping(value = { "loginTest", "" })
	public void loginTest(Model model, @Identify Integer userId, HttpServletResponse response) {
		try {
			PrintWriter writer = response.getWriter();
			response.reset();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html");
			writer.println(userId!=null?userId:0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = { "search" })
	public String search(Model model) {
		model.addAttribute("search", "Search Smile SApp!");
		return "index/search";
	}

	@RequestMapping(value = { "version" })
	public String version(Model model) {
		model.addAttribute("versionModel", versionService.getLastestModel());
		return "index/version";
	}

	@RequestMapping(value = { "versionUpdate" })
	public String versionUpdate(Model model, VersionModel versionModel, MultipartFile file) {
		Integer code = 200000;
		String message = "";
		try {
			versionService.insertVersion(versionModel, file);
		} catch (ValidateException e) {
			e.printStackTrace();
			code = e.getCode();
			message = e.getMessage();
		}

		if (code.equals(200000)) {
			model.addAttribute("result", code);
		} else {
			model.addAttribute("result", code + (message != null ? message : ""));
		}
		return "index/version";
	}
}
