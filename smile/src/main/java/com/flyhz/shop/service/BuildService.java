
package com.flyhz.shop.service;

import java.util.List;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.shop.dto.OrderDto;

/**
 * build接口，用于build数据到solr和redis
 * 
 * @author zhangb 2014年4月1日 下午2:20:12
 * 
 */
public interface BuildService {

	/**
	 * build数据到solr和redis
	 */
	public void buildData();

	/**
	 * build数据到solr
	 */
	public void buildSolr();

	/**
	 * build数据到redis
	 */
	public void buildRedis();

	/**
	 * 从redis获取指定订单
	 * 
	 * @param userId
	 *            用户ID，不能null
	 * @param orderId
	 * @return
	 */
	public OrderDto getOrder(Integer userId, Integer orderId) throws ValidateException;

	/**
	 * 获取指定用户的所有订单
	 * 
	 * @param userId
	 *            用户ID，不能null
	 * @return
	 */
	public List<OrderDto> getOrders(Integer userId) throws ValidateException;

	/**
	 * 获取当前美元汇率
	 * 
	 * @return
	 */
	public double getDollarExchangeRate();
}
