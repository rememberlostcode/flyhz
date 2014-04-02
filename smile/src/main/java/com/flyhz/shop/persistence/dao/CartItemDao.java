
package com.flyhz.shop.persistence.dao;

import java.util.List;

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
	public int deleteCartItem(CartitemModel cartitemModel);

	/**
	 * 查找购物车商品
	 * 
	 * @param cartItem
	 * @return cartItem
	 */
	public CartitemModel getCartItemById(CartitemModel cartitemModel);

	/**
	 * 查找购物车商品
	 * 
	 * @param cartItem
	 * @return cartItem
	 */
	public CartitemModel getCartItemByProductId(CartitemModel cartitemModel);

	/**
	 * 更新购物车商品
	 * 
	 * @param cartItem
	 * @return int
	 */
	public int updateCartItem(CartitemModel cartitemModel);

	/**
	 * 插入购物车商品
	 * 
	 * @param cartItem
	 * @return int
	 */
	public int insertCartItem(CartitemModel cartitemModel);

	/**
	 * 查询用户购物车商品列表
	 * 
	 * @param cartItem
	 * @return List<CartitemModel>
	 */
	public List<CartitemModel> getCartItemList(CartitemModel cartitemModel);
}
