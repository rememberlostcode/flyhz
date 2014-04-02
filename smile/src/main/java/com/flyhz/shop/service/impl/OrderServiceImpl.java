
package com.flyhz.shop.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.shop.dto.OrderDetailDto;
import com.flyhz.shop.dto.OrderDto;
import com.flyhz.shop.dto.ProductDto;
import com.flyhz.shop.dto.UserDto;
import com.flyhz.shop.persistence.dao.ConsigneeDao;
import com.flyhz.shop.persistence.dao.OrderDao;
import com.flyhz.shop.persistence.dao.ProductDao;
import com.flyhz.shop.persistence.dao.UserDao;
import com.flyhz.shop.persistence.entity.ConsigneeModel;
import com.flyhz.shop.persistence.entity.OrderModel;
import com.flyhz.shop.persistence.entity.UserModel;
import com.flyhz.shop.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {
	private Logger			log	= LoggerFactory.getLogger(OrderServiceImpl.class);
	@Resource
	private OrderDao		orderDao;
	@Resource
	private UserDao			userDao;
	@Resource
	private ConsigneeDao	consigneeDao;
	@Resource
	private ProductDao		productDao;

	@Override
	public String generateOrder(Integer userId, Integer consigneeId, String[] productIds)
			throws ValidateException {
		if (userId == null)
			throw new ValidateException("您没有登录！");
		if (consigneeId == null)
			throw new ValidateException("收件人地址为空！");
		if (productIds == null || productIds.length <= 0)
			throw new ValidateException("产品ID为空！");

		UserModel user = userDao.getModelById(userId);
		if (user == null)
			throw new ValidateException("您还不是会员，请注册后再购买！");

		ConsigneeModel consignee = consigneeDao.getModelById(consigneeId);
		if (consignee == null)
			throw new ValidateException("收件人地址为空！");

		List<OrderDetailDto> orderDetails = new ArrayList<OrderDetailDto>();
		for (String pidstr : productIds) {
			if (StringUtils.isBlank(pidstr))
				continue;
			try {
				String[] pid_qty = pidstr.split("_");// 格式是pid_qty,如：1_2
				int qty = Integer.parseInt(pid_qty[1]);
				if (qty <= 0)
					continue;

				ProductDto product = productDao.getProductById(Integer.parseInt(pid_qty[0]));
				if (product != null) {
					OrderDetailDto orderDetailDto = new OrderDetailDto();
					orderDetailDto.setProduct(product);
					orderDetailDto.setQty((short) qty);
					if (product.getPurchasingPrice() != null) {
						orderDetailDto.setTotal(product.getPurchasingPrice().multiply(
								BigDecimal.valueOf(qty)));
					}
				}
			} catch (Exception e) {
				log.error("生成订单出错：", e.getMessage());
				continue;
			}
		}

		OrderDto orderDto = new OrderDto();

		OrderModel order = new OrderModel();
		order.setUserId(userId);
		order.setStatus('0');
		// orderDao.generateOrder(order);
		return null;
	}

	@Override
	public OrderDto getOrder(Integer userId, Integer orderId) {
		OrderModel order = new OrderModel();
		order.setId(orderId);
		order.setUserId(userId);
		OrderModel orderModel = orderDao.getModel(order);
		if (orderModel != null) {
			OrderDto orderDto = convertOrderModelToOrderDto(orderModel);
			return orderDto;
		}
		return null;
	}

	@Override
	public List<OrderDto> listOrders(Integer userId, Character status) {
		OrderModel order = new OrderModel();
		order.setUserId(userId);
		order.setStatus(status);
		List<OrderModel> orderList = orderDao.getModelList(order);
		if (orderList != null && !orderList.isEmpty()) {
			List<OrderDto> orderDtoList = new ArrayList<OrderDto>();
			for (OrderModel orderModel : orderList) {
				OrderDto orderDto = convertOrderModelToOrderDto(orderModel);
				orderDtoList.add(orderDto);
			}
			return orderDtoList;
		}
		return null;
	}

	@Override
	public OrderDto pay() {
		// TODO Auto-generated method stub
		return null;
	}

	private OrderDto convertOrderModelToOrderDto(OrderModel orderModel) {
		if (orderModel == null)
			return null;
		OrderDto orderDto = new OrderDto();
		orderDto.setId(orderModel.getId());
		orderDto.setTotal(orderModel.getTotal());
		UserDto user = new UserDto();
		user.setId(orderModel.getUserId());
		return orderDto;
	}

}