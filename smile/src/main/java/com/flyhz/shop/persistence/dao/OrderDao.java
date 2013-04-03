
package com.flyhz.shop.persistence.dao;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.shop.dto.OrderPayDto;
import com.flyhz.shop.persistence.entity.OrderModel;

public interface OrderDao extends GenericDao<OrderModel> {

	public void generateOrder(OrderModel orderModel) throws ValidateException;

	public OrderPayDto getOrderPay(OrderPayDto orderPayDto);

}
