
package com.flyhz.shop.service;

import java.util.List;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.shop.dto.OrderDto;
import com.flyhz.shop.dto.OrderPayDto;
import com.flyhz.shop.dto.OrderSimpleDto;
import com.flyhz.shop.persistence.entity.OrderModel;

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
	public OrderDto generateOrder(Integer userId, Integer consigneeId, String[] productId,
			boolean flag) throws ValidateException;

	/**
	 * 获取订单
	 * 
	 * @param userId
	 * @param orderId
	 * @return
	 * @throws ValidateException
	 */
	public String getOrder(Integer userId, Integer orderId) throws ValidateException;

	/**
	 * 获取指定用户的订单
	 * 
	 * @param userId
	 * @param status
	 * @return
	 * @throws ValidateException
	 */
	public List<OrderDto> listOrders(Integer userId, String status) throws ValidateException;

	/**
	 * 支付成功后的操作
	 * 
	 * @return
	 */
	public boolean pay(Integer userId, String number) throws ValidateException;

	/**
	 * 关闭订单
	 * 
	 * @param userId
	 * @param id
	 * @throws ValidateException
	 */
	public void closeOrder(Integer userId, Integer id) throws ValidateException;

	public OrderPayDto getOrderPay(OrderPayDto orderPayDto);

	/**
	 * 通过订单号获取订单
	 * 
	 * @param number
	 * @return
	 */
	public OrderSimpleDto getOrderDtoByNumber(String number);

	/**
	 * 订单支付成功后发送邮件
	 * 
	 * @param number
	 */
	public void sendPaySuccess(String number);

	/**
	 * 通过淘宝订单号获取付款状态
	 * 
	 * @param numbers
	 * 订单编号，多个时以逗号分隔
	 * @param tid
	 * @return
	 * @throws ValidateException
	 */
	public String getOrderPayStatusByTid(String numbers, Long tid) throws ValidateException;

	/**
	 * 通过订单编号修改状态（更新数据库，不改变solr中的状态）
	 * 
	 * @param orderModel
	 */
	public void updateStatusByNumber(OrderModel orderModel);
	
	/**
	 * 通过订单编号修改状态（会更新solr中的状态，发货发送通知；已付款会发送邮件；已完成只改状态）
	 * @param orderModel
	 */
	public void updateStatusByNumberForMessage(OrderModel orderModel);

}