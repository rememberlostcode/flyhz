
package com.flyhz.framework.lang;

import java.util.Date;

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
	 * @param gmtModify
	 *            订单修改时间
	 * @param status
	 * @param orderDetal
	 *            订单详情
	 * @throws ValidateException
	 */
	public void buildOrderToRedis(Integer userId, Integer orderId, String status, Date gmtModify,
			String orderDetal) throws ValidateException;

	/**
	 * 把订单build到redis（订单付款后使用）
	 * 
	 * @param tid
	 * @param userId
	 *            用户ID
	 * @param orderId
	 *            订单ID
	 * @param status
	 * @throws ValidateException
	 */
	public void reBuildOrderToRedis(Long tid, Integer userId, Integer orderId, String status)
			throws ValidateException;

	/**
	 * 缓存订单到redis及更新商品的销售量（增量方式）
	 */
	public void chacheOrders();

}
