
package com.flyhz.shop.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.shop.dto.OrderDto;
import com.flyhz.shop.persistence.dao.OrderDao;
import com.flyhz.shop.persistence.entity.OrderModel;
import com.flyhz.shop.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {
	@Resource
	private OrderDao	orderDao;

	@Override
	public OrderDto generateOrder(OrderDto order) throws ValidateException {
		if (order == null || StringUtils.isBlank(order.getDetails()))
			throw new ValidateException("订单不能为空");
		if (order.getUser().getId() == null)
			throw new ValidateException("您没有登录！");
		orderDao.generateOrder(order);
		return null;
	}

	@Override
	public OrderDto getOrder(Integer userId, Integer orderId) {
		OrderModel order = new OrderModel();
		order.setId(orderId);
		OrderModel orderModel = orderDao.getModel(order);
		if (orderModel != null) {
			OrderDto orderDto = new OrderDto();
			orderDto.setId(orderModel.getId());
			orderDto.setDetails(orderModel.getDetail());
			orderDto.setStatus(orderModel.getStatus());
			orderDto.setTotal(orderModel.getTotal());
			return orderDto;
		}
		return null;
	}

	@Override
	public List<OrderDto> listOrders(Integer userId, Character status) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrderDto pay() {
		// TODO Auto-generated method stub
		return null;
	}

}