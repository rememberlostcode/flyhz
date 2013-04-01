
package com.flyhz.shop.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.shop.persistence.dao.CartItemDao;
import com.flyhz.shop.persistence.entity.CartitemModel;
import com.flyhz.shop.service.ShoppingCartService;

public class ShoopingCartServiceImpl implements ShoppingCartService {
	@Resource
	private CartItemDao	cartItemDao;

	@Override
	public void addItem(Integer userId, Integer productId, Byte qty) throws ValidateException {
		if (productId == null) {
			throw new ValidateException("");
		}
		if (qty == null) {
			throw new ValidateException("");
		}
		if (qty <= 0) {
			throw new ValidateException("");
		}
		// 查询购物车是否已有该物品
		CartitemModel cartitemModel = new CartitemModel();
		cartitemModel.setProductId(productId);
		cartitemModel.setUserId(userId);
		cartitemModel = cartItemDao.getCartItem(cartitemModel);
		if (cartitemModel == null) {

		} else {

		}
	}

	@Override
	public void removeItem(Integer userId, Integer itemId) throws ValidateException {
		if (userId == null) {
			throw new ValidateException("");
		}
		if (itemId == null) {
			throw new ValidateException("");
		}
		CartitemModel cartitemModel = new CartitemModel();
		cartitemModel.setUserId(userId);
		cartitemModel.setId(itemId);
		cartItemDao.deleteCartItem(cartitemModel);
	}

	@Override
	public List<CartitemModel> listItems(Integer userId) {
		return null;
	}

	@Override
	public void setQty(Integer userId, Integer itemId, Byte qty) throws ValidateException {
		if (userId == null) {
			throw new ValidateException("");
		}
		if (itemId == null) {
			throw new ValidateException("");
		}
		if (qty == null || qty <= 0) {
			throw new ValidateException("");
		}
		CartitemModel cartitemModel = new CartitemModel();
		cartitemModel.setUserId(userId);
		cartitemModel.setId(itemId);
		cartitemModel = cartItemDao.getCartItem(cartitemModel);
		if (cartitemModel != null) {
			cartitemModel.setQty(qty);
			cartitemModel.setGmtModify(new Date());
		}
	}
}