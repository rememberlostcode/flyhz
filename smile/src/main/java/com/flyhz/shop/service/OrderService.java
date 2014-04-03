
package com.flyhz.shop.service;

import java.util.List;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.shop.dto.OrderDto;
import com.flyhz.shop.dto.OrderPayDto;

public interface OrderService {

	/**
	 * 生成订单
	 * 
	 * @param productId
	 *            产品ID
	 * @param consigneeId
	 *            收件人ID
	 * @param qty
	 * @param flag
	 *            说明：flag为true即要生成订单，为false不生成订单
	 * @return
	 * @throws ValidateException
	 */
	public String generateOrder(Integer userId, Integer consigneeId, String[] productId,
			boolean flag) throws ValidateException;

	public OrderDto getOrder(Integer userId, Integer orderId);

	public List<OrderDto> listOrders(Integer userId, Character status);

	public OrderDto pay();

	public OrderPayDto getOrderPay(OrderPayDto orderPayDto);

}