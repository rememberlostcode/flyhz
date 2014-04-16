
package com.holding.smile.protocol;

import java.util.List;

import com.holding.smile.entity.Consignee;

public class PConsignees extends PBase {

	private List<Consignee>	data;

	public List<Consignee> getData() {
		return data;
	}

	public void setData(List<Consignee> data) {
		this.data = data;
	}

}
