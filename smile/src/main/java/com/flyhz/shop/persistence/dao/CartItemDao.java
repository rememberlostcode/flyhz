
package com.flyhz.shop.persistence.dao;

import com.flyhz.shop.persistence.entity.CartitemModel;

/**
 * 购物车DAO
 * 
 * @author fuwb 20140401
 */
public interface CartItemDao extends GenericDao<CartitemModel> {
	/**
	 * 删除购物车商品
	 * 
	 * @param cartItem
	 * @return int
	 */
	public int deleteCartItem(CartitemModel cartItem);

	/**
	 * 查找购物车商品
	 * 
	 * @param cartItem
	 * @return cartItem
	 */
	public CartitemModel getCartItem(CartitemModel cartItem);

	/**
	 * 更新购物车商品
	 * 
	 * @param cartItem
	 * @return int
	 */
	public int updateCartItem(CartitemModel cartItem);

	/**
	 * 插入购物车商品
	 * 
	 * @param cartItem
	 * @return int
	 */
	public int insertCartItem(CartitemModel cartItem);
}
