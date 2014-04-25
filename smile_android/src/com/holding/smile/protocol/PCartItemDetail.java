
package com.holding.smile.protocol;

import com.holding.smile.entity.CartItem;

/**
 * 类说明：订单信息
 * 
 * @author robin 2014-4-22下午3:26:09
 * 
 */
public class PCartItemDetail extends PBase {

	private CartItem	data;

	public CartItem getData() {
		return data;
	}

	public void setData(CartItem data) {
		this.data = data;
	}

}
