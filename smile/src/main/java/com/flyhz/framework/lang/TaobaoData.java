
package com.flyhz.framework.lang;

import com.taobao.api.domain.Trade;

public interface TaobaoData {

	/**
	 * 同步订单及物流信息
	 */
	public void synchronizationLogistics();

	/**
	 * 通过淘宝订单号获取订单信息(包括buyer_nick买家昵称、payment实付金额、post_fee邮费、status状态)
	 * 
	 * @param tid
	 * @return
	 */
	public Trade getTradeByTid(Long tid);

	/**
	 * 获取淘宝卖家姓名
	 * 
	 * @param tid
	 * @return
	 */
	public String getReceiverName(Long tid);

	/**
	 * 启动淘宝消息进程，以便接收淘宝的信息
	 */
	public void startMessageHandler();

	/**
	 * 关闭淘宝消息进程
	 */
	public void stopMessageHandler();
}
