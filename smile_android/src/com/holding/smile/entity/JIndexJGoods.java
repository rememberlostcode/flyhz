
package com.holding.smile.entity;

import java.io.Serializable;
import java.util.List;

import com.holding.smile.dto.BrandJGoods;

public class JIndexJGoods implements Serializable {

	private static final long	serialVersionUID	= 1L;
	private List<JActivity>		activity;
	private List<BrandJGoods>	brand;

	public List<JActivity> getActivity() {
		return activity;
	}

	public void setActivity(List<JActivity> activity) {
		this.activity = activity;
	}

	public List<BrandJGoods> getBrand() {
		return brand;
	}

	public void setBrand(List<BrandJGoods> brand) {
		this.brand = brand;
	}

}
