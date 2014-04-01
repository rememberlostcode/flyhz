
package com.flyhz.shop.persistence.dao;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.shop.dto.OrderDto;
import com.flyhz.shop.persistence.entity.OrderModel;

public interface OrderDao extends GenericDao<OrderModel> {

	public void generateOrder(OrderDto order) throws ValidateException;

}
