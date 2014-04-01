
package com.flyhz.shop.service;

import java.util.List;

import com.flyhz.shop.dto.OrderDto;

public interface OrderService {

	public OrderDto generateOrder(OrderDto order);

	public OrderDto getOrder(Integer userId, Integer orderId);

	public List<OrderDto> listOrders(Integer userId, Character status);

	public OrderDto pay();

}