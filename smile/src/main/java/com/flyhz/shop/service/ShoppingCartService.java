
package com.flyhz.shop.service;

import java.util.List;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.shop.dto.CartItemDto;
import com.flyhz.shop.dto.CartItemParamDto;

public interface ShoppingCartService {
	/**
	 * 添加商品到购物车
	 * 
	 * @param userId
	 * @param productId
	 * @return
	 */
	public void addItem(Integer userId, Integer productId, Byte qty) throws ValidateException;

	/**
	 * 删除购物车物品
	 * 
	 * @param userId
	 * @param itemId
	 * @return
	 */
	public void removeItem(Integer userId, Integer itemId) throws ValidateException;

	/**
	 * 查询购物车物品列表
	 * 
	 * @param userId
	 * @return List<CartItemDto>
	 */
	public List<CartItemDto> listItems(Integer userId) throws ValidateException;

	/**
	 * 设置购物车商品数量
	 * 
	 * @param userId
	 * @param itemId
	 * @param qty
	 * @return
	 */
	public CartItemDto setQty(Integer userId, Integer itemId, Byte qty) throws ValidateException;

	/**
	 * 查询购物车物品列表
	 * 
	 * @param userId
	 * @return String[]
	 */
	public String[] listItemsByUserIdAndIds(CartItemParamDto cartItemParam);
}