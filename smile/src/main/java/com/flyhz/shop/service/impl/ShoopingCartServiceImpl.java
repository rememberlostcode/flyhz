
package com.flyhz.shop.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.shop.dto.CartItemDto;
import com.flyhz.shop.persistence.dao.CartItemDao;
import com.flyhz.shop.persistence.entity.CartitemModel;
import com.flyhz.shop.service.ShoppingCartService;

@Service
public class ShoopingCartServiceImpl implements ShoppingCartService {
	@Resource
	private CartItemDao	cartItemDao;

	@Override
	public void addItem(Integer userId, Integer productId, Byte qty) throws ValidateException {
		// 登陆用户ID不能为空
		if (userId == null) {
			throw new ValidateException("");
		}
		// 商品ID不能为空
		if (productId == null) {
			throw new ValidateException("");
		}
		// 商品数量错误
		if (qty == null || qty <= 0) {
			throw new ValidateException("");
		}
		// 查询购物车是否已有该物品
		CartitemModel cartitemModel = new CartitemModel();
		cartitemModel.setProductId(productId);
		cartitemModel.setUserId(userId);
		CartitemModel cartitemModelNew = cartItemDao.getCartItem(cartitemModel);
		// 购物车无此商品
		if (cartitemModelNew == null) {
			cartitemModel.setGmtCreate(new Date());
			cartitemModel.setQty(qty);
			cartItemDao.insertCartItem(cartitemModel);
		} else {
			// 购物车有此商品
			byte qtyNew = (byte) (cartitemModelNew.getQty() + qty);
			cartitemModelNew.setQty(qtyNew);
			cartitemModelNew.setGmtModify(new Date());
			cartItemDao.updateCartItem(cartitemModelNew);
		}
	}

	@Override
	public void removeItem(Integer userId, Integer itemId) throws ValidateException {
		// 登陆用户ID不能为空
		if (userId == null) {
			throw new ValidateException("");
		}
		// 购物车商品ID不能为空
		if (itemId == null) {
			throw new ValidateException("");
		}
		CartitemModel cartitemModel = new CartitemModel();
		cartitemModel.setUserId(userId);
		cartitemModel.setId(itemId);
		cartItemDao.deleteCartItem(cartitemModel);
	}

	@Override
	public List<CartItemDto> listItems(Integer userId) throws ValidateException {
		// 登陆用户ID不能为空
		if (userId == null) {
			throw new ValidateException("");
		}
		CartitemModel cartitemModel = new CartitemModel();
		cartitemModel.setUserId(userId);
		List<CartitemModel> cartItems = cartItemDao.getCartItemList(cartitemModel);
		if (cartItems != null && !cartItems.isEmpty()) {
			List<CartItemDto> cartItemDtos = new ArrayList<CartItemDto>();
			for (CartitemModel cartitemModelIte : cartItems) {
				cartItemDtos.add(getCartItemDto(cartitemModelIte));
			}
			return cartItemDtos;
		}
		return null;
	}

	// 处理CartitemModel，返回CartItemDto对象
	private CartItemDto getCartItemDto(CartitemModel cartitemModel) {
		if (cartitemModel != null) {
			CartItemDto cartItemDto = new CartItemDto();
			cartItemDto.setId(cartitemModel.getId());
			cartItemDto.setQty((short) cartitemModel.getQty());
			// product redis中取数据
		}
		return null;
	}

	@Override
	public void setQty(Integer userId, Integer itemId, Byte qty) throws ValidateException {
		// 登陆用户ID不能为空
		if (userId == null) {
			throw new ValidateException("");
		}
		// 购物车商品ID不能为空
		if (itemId == null) {
			throw new ValidateException("");
		}
		// 商品数量错误
		if (qty == null || qty <= 0) {
			throw new ValidateException("");
		}
		CartitemModel cartitemModel = new CartitemModel();
		cartitemModel.setUserId(userId);
		cartitemModel.setId(itemId);
		cartitemModel = cartItemDao.getCartItem(cartitemModel);
		// 购物车商品不能为空
		if (cartitemModel == null) {
			throw new ValidateException("");
		}
		cartitemModel.setQty(qty);
		cartitemModel.setGmtModify(new Date());
		cartItemDao.updateCartItem(cartitemModel);
	}
}