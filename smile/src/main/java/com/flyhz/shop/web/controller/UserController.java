
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
import com.flyhz.framework.util.DateUtil;
import com.flyhz.framework.util.StringUtil;
import com.flyhz.shop.dto.CartItemDto;
import com.flyhz.shop.dto.CartItemParamDto;
import com.flyhz.shop.dto.ConsigneeDetailDto;
import com.flyhz.shop.dto.OrderDto;
import com.flyhz.shop.dto.OrderPayDto;
import com.flyhz.shop.dto.ProductDto;
import com.flyhz.shop.dto.RtnOrderDto;
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

	@RequestMapping(value = { "saveRegister" }, method = RequestMethod.GET)
	public String saveRegister(Model model, @ModelAttribute UserDetailDto userDetail) {
		Protocol protocol = new Protocol();
		Integer code = 200000;
		UserDto user = null;
		if (userDetail != null) {
			if (StringUtils.isNotBlank(userDetail.getUsername())
					&& StringUtils.isNotBlank(userDetail.getPassword())) {
				try {
					user = userService.register(userDetail);
				} catch (ValidateException e) {
					code = e.getCode();
					log.error("=======在注册时报=========" + e.getMessage());
				}
			} else {
				code = 101029;// 用户名或者密码错误
			}
		} else {
			code = 101029;
		}
		protocol.setCode(code);
		if (user != null) {
			protocol.setData(user);
		}
		model.addAttribute("protocol", protocol);
		return "";
	}

	/**
	 * 查询收件人信息
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "user/consignees" }, method = RequestMethod.GET)
	public String getConsinee(Model model, @Identify Integer userId) {
		Protocol protocol = new Protocol();
		Integer code = 200000;
		if (userId == null)
			code = 101002;
		List<ConsigneeDetailDto> consigneeDtoList = null;
		try {
			if (code.equals(200000)) {
				consigneeDtoList = userService.listConsignees(userId);
			}
		} catch (Exception e) {
			code = 400000;
			e.printStackTrace();
			log.error("=======查询地址时=========" + e.getMessage());
		}
		protocol.setCode(code);
		protocol.setData(consigneeDtoList);
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
	public String updateQty(Model model, @Identify Integer userId, @RequestParam String pid,
			@RequestParam Integer qty) {
		Protocol protocol = new Protocol();
		Integer code = 200000;
		if (userId == null)
			code = 101002;
		if (pid == null)
			code = 101021;
		ProductDto product = null;
		try {
			if (code.equals(200000)) {
				if (qty == null || qty == 0)
					qty = 1;
				product = redistRepository.getProductFromRedis(pid);
				if (product != null) {
					product.setQty(qty.shortValue());
					product.setPurchasingPrice(product.getPurchasingPrice().multiply(
							BigDecimal.valueOf(qty)));
				} else {
					code = 101021;// 产品不存在
				}
			}
		} catch (Exception e) {
			code = 400000;// 错误
			log.error("=======在修改购买数量时=========" + e.getMessage());
		}
		protocol.setCode(code);
		protocol.setData(product);
		model.addAttribute("protocol", protocol);
		return "";
	}

	/**
	 * 针对从购物车结算：不能修改订单商品数量,这时还没有生成订单
	 * 
	 * @param model
	 * @param orderDto
	 * @return
	 */
	@RequestMapping(value = { "user/updateCartQty" })
	public String updateCartQty(Model model, @Identify Integer userId, @RequestParam String id,
			@RequestParam Integer qty) {
		Protocol protocol = new Protocol();
		Integer code = 200000;
		if (userId == null)
			code = 101002;
		if (id == null)
			code = 101021;
		CartItemDto cartItemDto = null;
		try {
			if (code.equals(200000)) {
				if (qty == null || qty == 0)
					qty = 1;
				cartItemDto = shoppingCartService.setQty(userId, Integer.valueOf(id),
						qty.byteValue());
			}
		} catch (ValidateException e) {
			code = e.getCode();
			log.error("=======在修改购买数量时=========" + e.getMessage());
		}
		protocol.setCode(code);
		protocol.setData(cartItemDto);
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
	public String orderInform(Model model, @Identify Integer userId, String[] pids,
			Integer[] cartIds, Integer cid) {
		Protocol protocol = new Protocol();
		Integer code = 200000;
		if (userId == null)
			code = 101002;
		OrderDto orderDto = null;
		try {
			if ((pids == null || pids.length == 0) && (cartIds == null || cartIds.length == 0))
				code = 101021;

			if (code.equals(200000)) {
				if (cartIds != null && cartIds.length > 0) {
					CartItemParamDto cartItemParam = new CartItemParamDto();
					cartItemParam.setUserId(userId);
					cartItemParam.setItemIds(cartIds);
					pids = shoppingCartService.listItemsByUserIdAndIds(cartItemParam);
				}
				orderDto = orderService.generateOrder(userId, cid, pids, false);
			}
		} catch (ValidateException e) {
			code = e.getCode();
			log.error("=======在生成订单时=========" + e.getMessage());
		}
		protocol.setCode(code);
		protocol.setData(orderDto);
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
	public String confirmOrder(Model model, @Identify Integer userId, String[] pids,
			Integer[] cartIds, Integer cid) {
		Protocol protocol = new Protocol();
		Integer code = 200000;
		if (userId == null)
			code = 101002;
		try {
			if (((pids == null || pids.length == 0) && (cartIds == null || cartIds.length == 0)))
				code = 101021;

			if (code.equals(200000)) {
				if (cartIds != null && cartIds.length > 0) {
					CartItemParamDto cartItemParam = new CartItemParamDto();
					cartItemParam.setUserId(userId);
					cartItemParam.setItemIds(cartIds);
					pids = shoppingCartService.listItemsByUserIdAndIds(cartItemParam);
				}

				OrderDto details = orderService.generateOrder(userId, cid, pids, true);
				if (details != null) {
					OrderPayDto orderPayDto = new OrderPayDto();
					orderPayDto.setUserId(userId);
					orderPayDto.setId(details.getId());
					orderPayDto = orderService.getOrderPay(orderPayDto);

					RtnOrderDto rtnOrder = new RtnOrderDto();
					rtnOrder.setNumber(orderPayDto.getNumber());
					rtnOrder.setTime(DateUtil.dateToStr(orderPayDto.getGmtCreate()));
					rtnOrder.setTotal(orderPayDto.getTotal());
					protocol.setData(rtnOrder);
				} else {
					code = 400000;
					log.error("=======在生成订单时=========出现异常！");
				}
			}
		} catch (ValidateException e) {
			code = e.getCode();
			log.error("=======在生成订单时=========" + e.getMessage());
		}
		protocol.setCode(code);
		model.addAttribute("protocol", protocol);
		return "";
	}

	/**
	 * 支付成功后返回结果状态
	 * 
	 * @param model
	 * @param orderDto
	 * @return
	 */
	@RequestMapping(value = { "user/pay" })
	public String pay(Model model, @Identify Integer userId, @RequestParam String num) {
		Protocol protocol = new Protocol();
		Integer code = 200000;
		if (userId == null)
			code = 101002;
		if (StringUtil.isBlank(num))
			code = 201001;

		try {
			// true代表已付款，false代表未付款
			if (!orderService.pay(userId, num)) {
				code = 300000;
			}
		} catch (ValidateException e) {
			code = 400000;
			log.error("=======在付款时报=========" + e.getMessage());
		}
		protocol.setCode(code);
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
	// @RequestMapping(value = "user/setInfo", method = RequestMethod.POST)
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
	// @RequestMapping(value = "user/releiveEmail", method = RequestMethod.POST)
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

	/**
	 * 查询用户个人信息
	 * 
	 * @param userId
	 * @param model
	 */
	@RequestMapping(value = "user/getPInfo", method = RequestMethod.GET)
	public void getPersonalInfo(@Identify Integer userId, Model model) {
		Protocol protocol = new Protocol();
		try {
			protocol.setData(userService.getPersonalInformation(userId));
			protocol.setCode(200000);
		} catch (ValidateException e) {
			protocol.setCode(e.getCode());
		}
		model.addAttribute("protocol", protocol);
	}

	/**
	 * 重置用户密码
	 * 
	 * @param userId
	 * @param oldpwd
	 * @param newpwd
	 * @param model
	 */
	// @RequestMapping(value = "user/resetPwd", method = RequestMethod.POST)
	@RequestMapping(value = "user/resetPwd")
	public void resetPwd(@Identify Integer userId, @RequestParam(value = "opwd") String oldpwd,
			@RequestParam(value = "npwd") String newpwd, Model model) {
		Protocol protocol = new Protocol();
		try {
			userService.resetpwd(userId, oldpwd, newpwd);
			protocol.setCode(200000);
		} catch (ValidateException e) {
			protocol.setCode(e.getCode());
		}
		model.addAttribute("protocol", protocol);
	}

	@RequestMapping(value = "user/findPwd", method = RequestMethod.GET)
	public void findPwd(String username, Model model) {
		Protocol protocol = new Protocol();
		try {
			protocol.setData(userService.findPwd(username));
			protocol.setCode(200000);
		} catch (ValidateException e) {
			protocol.setCode(e.getCode());
		}
		model.addAttribute("protocol", protocol);
	}
}
