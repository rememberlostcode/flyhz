
package com.flyhz.shop.service.impl;

import java.util.List;

import com.flyhz.shop.dto.CartItem;
import com.flyhz.shop.service.ShoppingCartService;

public class ShoopingCartServiceImpl implements ShoppingCartService {
	@Override
	public void addItem(Integer userId, Integer productId, Integer qty) {

	}

	@Override
	public void removeItem(Integer userId, Integer itemId) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<CartItem> listItems(Integer userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setQty(Integer userId, Integer itemId, Integer qty) {
		// TODO Auto-generated method stub

	}
}