
package com.flyhz.shop.web.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import com.flyhz.framework.auth.Identify;
import com.flyhz.framework.lang.ValidateException;
import com.flyhz.framework.lang.page.Pager;
import com.flyhz.framework.lang.page.Pagination;
import com.flyhz.shop.dto.PurchaseDto;
import com.flyhz.shop.dto.PurchasePageDto;
import com.flyhz.shop.service.BrandService;
import com.flyhz.shop.service.PurchaseService;

/**
 * 代购进度Controller
 * 
 * @author Administrator
 */
@Controller
@RequestMapping(value = "/purchase")
public class PurchaseController {
	@Resource
	private PurchaseService	purchaseService;
	@Resource
	private BrandService	brandService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String listPurchaseSchedules(@Pagination Pager pager, PurchasePageDto purchaseParam,
			Model model) {
		// 查询品牌列表
		model.addAttribute("brands", brandService.getAllBrandBuildDtos());
		model.addAttribute("purchaseParam", purchaseParam);
		model.addAttribute("page", pager);
		model.addAttribute("current", "2");
		List<PurchaseDto> purchaseScheduleDtos = purchaseService.getPurchaseDtosPage(pager,
				purchaseParam);
		model.addAttribute("purchases", purchaseScheduleDtos);
		return "/purchase/list";
	}

	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public String showPurchase(@RequestParam(value = "purchaseId") Integer purchaseId, Model model) {
		PurchaseDto purchaseDto = purchaseService.getPurchaseDto(purchaseId);
		model.addAttribute("purchase", purchaseDto);
		return "/purchase/show";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String editPurchase(@Identify Integer userId, PurchaseDto purchase, Model model) {
		try {
			purchaseService.editPurchase(userId, purchase);
			model.addAttribute("message", "操作成功");
		} catch (ValidateException e) {
			model.addAttribute("message", e.getMessage());
		}
		return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/purchase/list";
	}
}
