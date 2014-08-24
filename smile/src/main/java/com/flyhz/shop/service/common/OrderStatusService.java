
package com.flyhz.shop.service.common;

import java.math.BigDecimal;

public interface OrderStatusService {

	/**
	 * 关闭订单10->50
	 * 
	 * @param tid
	 * @param numbers
	 */
	public void closeOrderById(Long tid, String[] numbers);

	/**
	 * 已付款并验证金额和身份证 10->12/13/14/15/16
	 * 
	 * @param numbers
	 * @param payment
	 * @param taobaoReceiverName
	 * @param tid
	 * @return
	 */
	public String paymentValidateAmountAndIdcard(String[] numbers, BigDecimal payment,
			String taobaoReceiverName, Long tid);

	/**
	 * 发货 16->20
	 * 
	 * @param numbers
	 */
	public void sendGoods(String[] numbers);

	/**
	 * 收货 20/40->60
	 * 
	 * @param numbers
	 */
	public void receiveGoods(String[] numbers);
}
