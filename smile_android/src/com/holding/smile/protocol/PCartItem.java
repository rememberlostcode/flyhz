
package com.holding.smile.protocol;

import java.util.List;

import com.holding.smile.entity.CartItem;

/**
 * 类说明：订单信息
 * 
 * @author robin 2014-4-22下午3:26:09
 * 
 */
public class PCartItem extends PBase {

	private List<CartItem>	data;

	public List<CartItem> getData() {
		return data;
	}

	public void setData(List<CartItem> data) {
		this.data = data;
	}

}
