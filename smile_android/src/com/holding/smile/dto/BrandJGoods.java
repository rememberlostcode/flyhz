
package com.holding.smile.dto;

import java.util.List;

import com.holding.smile.entity.JGoods;

/**
 * 返回值对象
 * 
 * @author robin 2013-12-28 上午11:26:19
 * 
 */
public class BrandJGoods {
	private String			brandName;	// 品牌名
	private List<JGoods>	goodData;	// 返回数据

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public List<JGoods> getGoodData() {
		return goodData;
	}

	public void setGoodData(List<JGoods> goodData) {
		this.goodData = goodData;
	}

}
