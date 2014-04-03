
package com.flyhz.framework.lang;

import com.flyhz.shop.dto.ProductDto;

/**
 * redis获取数据
 * 
 * @author zhangb 2014年4月2日 下午6:24:01
 * 
 */
public interface RedisRepository {

	/**
	 * 从redis获得单个物品信息
	 * 
	 * @param productId
	 *            物品ID
	 * @return
	 * @throws ValidateException
	 */
	public ProductDto getProductFromRedis(String productId) throws ValidateException;

	/**
	 * 从redis获得单个订单信息
	 * 
	 * @param userId
	 *            用户ID
	 * @param orderId
	 *            订单ID
	 * @return
	 * @throws ValidateException
	 */
	public String getOrderFromRedis(Integer userId, Integer orderId) throws ValidateException;

	/**
	 * 把订单build到redis（确认订单后使用）
	 * 
	 * @param userId
	 *            用户ID
	 * @param orderId
	 *            订单ID
	 * @param orderDetal
	 *            订单详情
	 * @throws ValidateException
	 */
	public void buildOrderToRedis(Integer userId, Integer orderId, String orderDetal)
			throws ValidateException;

	/**
	 * 把订单build到redis（订单付款后使用）
	 * 
	 * @param userId
	 *            用户ID
	 * @param orderId
	 *            订单ID
	 * @throws ValidateException
	 */
	public void reBuildOrderToRedis(Integer userId, Integer orderId) throws ValidateException;
}
