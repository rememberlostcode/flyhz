
package com.holding.smile.protocol;

import com.holding.smile.dto.OrderPayDto;

/**
 * 类说明：支付状态
 * 
 * @author fuwb 2014-7-16
 */
public class PPayStatus extends PBase {
	private OrderPayDto	data;

	public OrderPayDto getData() {
		return data;
	}

	public void setData(OrderPayDto data) {
		this.data = data;
	}
}
