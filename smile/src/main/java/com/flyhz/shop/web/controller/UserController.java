
package com.flyhz.shop.web.controller;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.flyhz.framework.auth.Identify;
import com.flyhz.framework.lang.Protocol;
import com.flyhz.framework.lang.RedisRepository;
import com.flyhz.framework.lang.ValidateException;
import com.flyhz.framework.util.JSONUtil;
import com.flyhz.shop.dto.CartItemDto;
import com.flyhz.shop.dto.CartItemParamDto;
import com.flyhz.shop.dto.ConsigneeDetailDto;
import com.flyhz.shop.dto.ProductDto;
import com.flyhz.shop.dto.UserDetailDto;
import com.flyhz.shop.dto.UserDto;
import com.flyhz.shop.service.OrderService;
import com.flyhz.shop.service.ShoppingCartService;
import com.flyhz.shop.service.UserService;

/**
 * 
 * 类说明：用户管理
 * 
 * @author robin 2014-4-1下午12:11:14
 * 
 */
@Controller
@RequestMapping(value = "/")
public class UserController {
	protected Logger			log	= LoggerFactory.getLogger(UserController.class);

	@Resource
	private UserService			userService;
	@Resource
	private OrderService		orderService;
	@Resource
	private ShoppingCartService	shoppingCartService;
	@Resource
	private RedisRepository		redistRepository;

	@RequestMapping(value = { "register" })
	public String register(Model model) {
		return "register";
	}

	@RequestMapping(value = { "saveRegister" }, method = RequestMethod.POST)
	public String saveRegister(Model model, @ModelAttribute UserDetailDto userDetail) {
		Protocol protocol = new Protocol();
		Integer code = 0;
		String msg = "";
		if (userDetail != null) {
			if (StringUtils.isNotBlank(userDetail.getUsername())
					&& StringUtils.isNotBlank(userDetail.getPassword())) {
				try {
					UserDto user = userService.register(userDetail);
					code = 1;
				} catch (ValidateException e) {
					code = 4;
					msg = e.getMessage();
					log.error("=======在注册时=========" + e.getMessage());
				}
			} else {
				code = 3;// 用户名或者密码错误
			}
		} else {
			code = 2;
		}
		protocol.setCode(code);
		protocol.setData(msg);
		model.addAttribute("protocol", protocol);
		return "";
	}

	@RequestMapping(value = { "user/consignees" })
	public String getConsinee(Model model) {
		Protocol protocol = new Protocol();
		Integer code = 0;
		List<ConsigneeDetailDto> consigneeDtoList = null;
		try {
			consigneeDtoList = userService.listConsignees(1);
		} catch (Exception e) {
			code = 4;
			e.printStackTrace();
			log.error("=======查询地址时=========" + e.getMessage());
		}
		protocol.setCode(code);
		protocol.setData(JSONUtil.getEntity2Json(consigneeDtoList));
		model.addAttribute("protocol", protocol);
		return "";
	}

	/**
	 * 针对直接购买：修改订单商品数量,这时还没有生成订单
	 * 
	 * @param model
	 * @param orderDto
	 * @return
	 */
	@RequestMapping(value = { "user/updateQty" })
	public String updateQty(Model model, String pid, Integer qty) {
		Protocol protocol = new Protocol();
		Integer code = 0;
		if (pid == null)
			code = 5;
		if (qty == null || qty == 0)
			qty = 1;
		ProductDto product = null;
		try {
			product = redistRepository.getProductFromRedis(pid);
			if (product != null) {
				product.setQty(qty.shortValue());
				product.setPurchasingPrice(product.getPurchasingPrice().multiply(
						BigDecimal.valueOf(qty)));
			}
		} catch (Exception e) {
			code = 4;
			log.error("=======在修改购买数量时=========" + e.getMessage());
		}
		protocol.setCode(code);
		protocol.setData(JSONUtil.getEntity2Json(product));
		model.addAttribute("protocol", protocol);
		return "";
	}

	/**
	 * 针对直接购买：修改订单商品数量,这时还没有生成订单
	 * 
	 * @param model
	 * @param orderDto
	 * @return
	 */
	@RequestMapping(value = { "user/updateCartQty" })
	public String updateCartQty(Model model, String id, Integer qty) {
		Protocol protocol = new Protocol();
		Integer code = 0;

		Integer userId = 1;
		if (userId == null)
			code = 1;
		if (id == null)
			code = 5;
		if (qty == null || qty == 0)
			qty = 1;
		CartItemDto cartItemDto = null;
		try {
			cartItemDto = shoppingCartService.setQty(userId, Integer.valueOf(id), qty.byteValue());
		} catch (Exception e) {
			code = 4;
			log.error("=======在修改购买数量时=========" + e.getMessage());
		}
		protocol.setCode(code);
		protocol.setData(JSONUtil.getEntity2Json(cartItemDto));
		model.addAttribute("protocol", protocol);
		return "";
	}

	/**
	 * 订单确认信息,这时还没有生成订单
	 * 
	 * @param model
	 * @param orderDto
	 * @return
	 */
	@RequestMapping(value = { "user/orderInform" })
	public String orderInform(Model model, String[] pids, Integer[] cartIds) {
		Protocol protocol = new Protocol();
		Integer code = 0;
		String details = "";
		if ((pids == null || pids.length == 0) && (cartIds == null || cartIds.length == 0))
			code = 5;

		if (cartIds != null && cartIds.length > 0) {
			CartItemParamDto cartItemParam = new CartItemParamDto();
			cartItemParam.setUserId(2);
			cartItemParam.setItemIds(cartIds);
			pids = shoppingCartService.listItemsByUserIdAndIds(cartItemParam);
		}

		try {
			details = orderService.generateOrder(2, null, pids, false);
		} catch (ValidateException e) {
			code = 4;
			log.error("=======在生成订单时=========" + e.getMessage());
		}
		protocol.setCode(code);
		protocol.setData(details);
		model.addAttribute("protocol", protocol);
		return "";
	}

	/**
	 * 确认订单信息并生成订单
	 * 
	 * @param model
	 * @param orderDto
	 * @return
	 */
	@RequestMapping(value = { "user/confirmOrder" })
	public String confirmOrder(Model model, String[] pids, String[] cartIds,
			@RequestParam Integer cid) {
		Protocol protocol = new Protocol();
		Integer code = 0;
		String details = "";
		if (((pids == null || pids.length == 0) && (cartIds == null || cartIds.length == 0))
				|| cid == null)
			code = 5;

		try {
			details = orderService.generateOrder(1, cid, pids, true);
			code = 1;
		} catch (ValidateException e) {
			code = 4;
			log.error("=======在生成订单时=========" + e.getMessage());
		}
		protocol.setCode(code);
		protocol.setData(details);
		model.addAttribute("protocol", protocol);
		return "";
	}

	/**
	 * 设置用户信息，包括邮箱、密码和手机号
	 * 
	 * @param userId
	 * @param field
	 * @param fval
	 * @param model
	 */
	@RequestMapping(value = "user/setInfo")
	public void setInfo(@Identify Integer userId, @RequestParam(value = "field") String field,
			@RequestParam(value = "fval") Object fval, Model model) {
		Protocol protocol = new Protocol();
		try {
			userService.setPersonalInformation(userId, field, fval);
			protocol.setCode(200000);
		} catch (ValidateException e) {
			protocol.setCode(e.getCode());
		}
		model.addAttribute("protocol", protocol);
	}

	/**
	 * 解除绑定邮箱
	 * 
	 * @param userId
	 * @param model
	 */
	@RequestMapping(value = "user/releiveEmail")
	public void releiveEmail(@Identify Integer userId, Model model) {
		Protocol protocol = new Protocol();
		try {
			userService.relieveEmail(userId);
			protocol.setCode(200000);
		} catch (ValidateException e) {
			protocol.setCode(e.getCode());
		}
		model.addAttribute("protocol", protocol);
	}
}
