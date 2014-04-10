
package com.flyhz.shop.persistence.dao;

import java.util.List;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.shop.build.solr.SolrPage;
import com.flyhz.shop.dto.OrderPayDto;
import com.flyhz.shop.persistence.entity.OrderModel;

public interface OrderDao extends GenericDao<OrderModel> {

	public void generateOrder(OrderModel orderModel) throws ValidateException;

	public OrderPayDto getOrderPay(OrderPayDto orderPayDto);

	public int getFinshedOrdersCount(SolrPage page);

	public List<String> findFinshedOrders(SolrPage page);

}
