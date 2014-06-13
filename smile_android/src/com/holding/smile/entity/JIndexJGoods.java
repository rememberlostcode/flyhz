
package com.holding.smile.entity;

import java.io.Serializable;
import java.util.List;

import com.holding.smile.dto.BrandJGoods;

public class JIndexJGoods implements Serializable {

	private static final long	serialVersionUID	= 1L;
	private List<JActivity>		activity;
	private List<BrandJGoods>	brands;

	public List<JActivity> getActivity() {
		return activity;
	}

	public void setActivity(List<JActivity> activity) {
		this.activity = activity;
	}

	public List<BrandJGoods> getBrands() {
		return brands;
	}

	public void setBrands(List<BrandJGoods> brands) {
		this.brands = brands;
	}

}
