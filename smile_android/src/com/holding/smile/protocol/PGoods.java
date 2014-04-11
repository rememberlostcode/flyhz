
package com.holding.smile.protocol;

import java.util.List;

import com.holding.smile.entity.JGoods;

public class PGoods extends PBase {

	private List<JGoods>	data;

	public List<JGoods> getData() {
		return data;
	}

	public void setData(List<JGoods> data) {
		this.data = data;
	}
}
