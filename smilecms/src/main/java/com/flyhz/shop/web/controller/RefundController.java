
package com.flyhz.shop.web.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.flyhz.framework.lang.page.Pager;
import com.flyhz.framework.lang.page.Pagination;
import com.flyhz.shop.dto.RefundPageDto;
import com.flyhz.shop.persistence.entity.RefundModel;
import com.flyhz.shop.service.RefundService;

/**
 * 订单退款Controller
 * 
 * @author Administrator
 */
@Controller
@RequestMapping(value = "/refund")
public class RefundController {
	@Resource
	private RefundService	refundService;

	@RequestMapping(value = "/list")
	public String listRefunds(@Pagination Pager pager, RefundPageDto refundParam, Model model) {
		model.addAttribute("refundParam", refundParam);
		model.addAttribute("page", pager);
		model.addAttribute("current", "3");
		List<RefundModel> refunds = refundService.getRefundsPage(pager, refundParam);
		model.addAttribute("refunds", refunds);
		return "/refund/list";
	}
}
