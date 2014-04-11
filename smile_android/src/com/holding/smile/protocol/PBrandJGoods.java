
package com.holding.smile.protocol;

import java.util.List;

import com.holding.smile.dto.BrandJGoods;

public class PBrandJGoods extends PBase {

	private List<BrandJGoods>	data;

	public List<BrandJGoods> getData() {
		return data;
	}

	public void setData(List<BrandJGoods> data) {
		this.data = data;
	}
}
