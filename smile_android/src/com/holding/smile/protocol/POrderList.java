
package com.holding.smile.protocol;

import java.util.List;

import com.holding.smile.dto.OrderDto;

/**
 * 订单列表
 * 
 * @author zhangb 2014年4月24日 下午3:42:03
 * 
 */
public class POrderList extends PBase {

	private List<OrderDto>	data;

	public List<OrderDto> getData() {
		return data;
	}

	public void setData(List<OrderDto> data) {
		this.data = data;
	}

}
