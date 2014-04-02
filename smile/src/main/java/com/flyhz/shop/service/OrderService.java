
package com.flyhz.shop.service;

import java.util.List;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.shop.dto.OrderDto;

public interface OrderService {

	/**
	 * 生成订单
	 * 
	 * @param productId
	 *            产品ID
	 * @param consigneeId
	 *            收件人ID
	 * @param qty
	 * @return
	 * @throws ValidateException
	 */
	public String generateOrder(Integer userId, Integer consigneeId, String[] productIds)
			throws ValidateException;

	public OrderDto getOrder(Integer userId, Integer orderId);

	public List<OrderDto> listOrders(Integer userId, Character status);

	public OrderDto pay();

}