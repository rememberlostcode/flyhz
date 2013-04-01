
package com.flyhz.shop.service;

import java.util.List;

import com.flyhz.shop.dto.CartItem;

public interface ShoppingCartService {
	/**
	 * 添加商品到购物车
	 * 
	 * @author fuwb
	 * @param userId 用户ID
	 * @param productId 产品ID
	 * @return 
	 */
	public void addItem(Integer userId, Integer productId, Integer qty);

	public void removeItem(Integer userId, Integer itemId);

	public List<CartItem> listItems(Integer userId);

	public void setQty(Integer userId, Integer itemId, Integer qty);

}