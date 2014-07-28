
package com.flyhz.shop.web.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.flyhz.framework.auth.Identify;
import com.flyhz.framework.lang.Protocol;
import com.flyhz.framework.lang.ValidateException;
import com.flyhz.shop.persistence.entity.IdcardModel;
import com.flyhz.shop.service.IdcardService;

/**
 * 用户管理身份证
 * 
 * @author zhangb 2014年4月21日 上午10:39:22
 * 
 */
@Controller
@RequestMapping(value = "/idcard")
public class IdcardController {

	@Resource
	private IdcardService	idcardService;

	@RequestMapping(value = "/list")
	public void list(@Identify Integer userId, Model model) {
		Protocol protocol = new Protocol();
		try {
			protocol.setData(idcardService.getAllIdcardsByUserId(userId));
			protocol.setCode(200000);
		} catch (ValidateException e) {
			protocol.setCode(e.getCode());
		}
		model.addAttribute("protocol", protocol);
	}

	/**
	 * 删除身份证信息
	 * 
	 * @param userId
	 * @param id
	 * @param model
	 */
	@RequestMapping(value = "/delete")
	public void delete(@Identify Integer userId, @RequestParam(value = "id") Integer id, Model model) {
		Protocol protocol = new Protocol();
		try {
			idcardService.deleteIdcard(id, userId);
			protocol.setCode(200000);
		} catch (ValidateException e) {
			protocol.setCode(e.getCode());
		}
		model.addAttribute("protocol", protocol);
	}

	/**
	 * 保存身份证信息
	 * 
	 * @param userId
	 * @param model
	 * @param idcardModel
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public void save(@Identify Integer userId, Model model, IdcardModel idcardModel,
			MultipartFile file,MultipartFile backfile) {
		Protocol protocol = new Protocol();
		try {
			idcardModel.setUserId(userId);
			idcardService.saveIdcard(idcardModel, file,backfile);
			protocol.setCode(200000);
		} catch (ValidateException e) {
			protocol.setCode(e.getCode());
		}
		model.addAttribute("protocol", protocol);
	}
}
