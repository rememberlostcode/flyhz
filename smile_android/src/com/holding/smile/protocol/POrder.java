
package com.holding.smile.protocol;

import com.holding.smile.dto.OrderDto;

/**
 * 类说明：订单信息
 * 
 * @author robin 2014-4-22下午3:26:09
 * 
 */
public class POrder extends PBase {

	private OrderDto	data;

	public OrderDto getData() {
		return data;
	}

	public void setData(OrderDto data) {
		this.data = data;
	}

}
