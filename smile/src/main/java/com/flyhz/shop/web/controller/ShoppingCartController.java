
package com.flyhz.shop.web.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.flyhz.framework.auth.Identify;
import com.flyhz.framework.lang.Protocol;
import com.flyhz.framework.lang.ValidateException;
import com.flyhz.shop.dto.CartItemDto;
import com.flyhz.shop.service.ShoppingCartService;

/**
 * 购物车Controller
 * 
 * @author fuwb 20140403
 */
@Controller
@RequestMapping(value = "/cart")
public class ShoppingCartController {
	@Resource
	private ShoppingCartService	shoppingCartService;

	// @RequestMapping(value = "/add", method = RequestMethod.POST)
	@RequestMapping(value = "/add")
	public void addCartItem(@Identify Integer userId,
			@RequestParam(value = "pid") Integer productId, @RequestParam(value = "qty") Byte qty,
			Model model) {
		Protocol protocol = new Protocol();
		try {
			shoppingCartService.addItem(userId, productId, qty);
			protocol.setCode(200000);
		} catch (ValidateException e) {
			protocol.setCode(e.getCode());
		}
		model.addAttribute("protocol", protocol);
	}

	// @RequestMapping(value = "/add", method = RequestMethod.POST)
	@RequestMapping(value = "/list")
	public void list(@Identify Integer userId, Model model) {
		Protocol protocol = new Protocol();
		try {
			List<CartItemDto> cartItems = shoppingCartService.listItems(userId);
			protocol.setCode(200000);
			protocol.setData(cartItems);
		} catch (ValidateException e) {
			protocol.setCode(e.getCode());
		}
		model.addAttribute("protocol", protocol);
	}

	// @RequestMapping(value = "/remove", method = RequestMethod.POST)
	@RequestMapping(value = "/remove")
	public void removeCartItem(@Identify Integer userId,
			@RequestParam(value = "id") Integer itemId, Model model) {
		Protocol protocol = new Protocol();
		try {
			shoppingCartService.removeItem(userId, itemId);
			protocol.setCode(200000);
		} catch (ValidateException e) {
			protocol.setCode(e.getCode());
		}
		model.addAttribute("protocol", protocol);
	}

	// @RequestMapping(value = "/setQty", method = RequestMethod.POST)
	@RequestMapping(value = "/setQty")
	public void setCartItemQty(@Identify Integer userId,
			@RequestParam(value = "id") Integer itemId, @RequestParam(value = "qty") Byte qty,
			Model model) {
		Protocol protocol = new Protocol();
		try {
			protocol.setData(shoppingCartService.setQty(userId, itemId, qty));
			protocol.setCode(200000);
		} catch (ValidateException e) {
			protocol.setCode(e.getCode());
		}
		model.addAttribute("protocol", protocol);
	}
}
