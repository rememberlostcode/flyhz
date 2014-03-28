
package com.flyhz.shop.service;

import java.util.List;

import com.flyhz.shop.dto.Order;

public interface OrderService {

	public Order generateOrder(Order order);

	public Order getOrder(Integer userId, Integer orderId);

	public List<Order> listOrders(Integer userId, Character status);

	public Order pay();

}