
package com.flyhz.shop.persistence.dao;

import java.util.List;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.shop.build.solr.SolrPage;
import com.flyhz.shop.dto.OrderPayDto;
import com.flyhz.shop.persistence.entity.OrderModel;

public interface OrderDao extends GenericDao<OrderModel> {

	public void generateOrder(OrderModel orderModel) throws ValidateException;

	public OrderPayDto getOrderPay(OrderPayDto orderPayDto);

	/**
	 * 已完成订单总数
	 * 
	 * @param page
	 * @return
	 */
	public int getFinshedOrdersCount(SolrPage page);

	/**
	 * 已完成的订单集合
	 * 
	 * @param page
	 * @return
	 */
	public List<OrderModel> findFinshedOrders(SolrPage page);

	/**
	 * 所有订单总数
	 * 
	 * @param page
	 * @return
	 */
	public int getAllOrdersCount(SolrPage page);

	/**
	 * 所有的订单集合
	 * 
	 * @param page
	 * @return
	 */
	public List<OrderModel> findAllOrders(SolrPage page);

}
