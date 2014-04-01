
package com.flyhz.shop.service;

import java.util.List;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.shop.persistence.entity.CartitemModel;

public interface ShoppingCartService {
	/**
	 * 添加商品到购物车
	 * 
	 * @author fuwb
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

	public List<CartitemModel> listItems(Integer userId);

	/**
	 * 设置购物车商品梳理
	 * 
	 * @param userId
	 * @param itemId
	 * @param qty
	 * @return
	 */
	public void setQty(Integer userId, Integer itemId, Byte qty) throws ValidateException;
}