
package com.holding.smile.dto;

import java.util.List;

import com.holding.smile.entity.Brand;
import com.holding.smile.entity.JGoods;

/**
 * 返回值对象
 * 
 * @author robin 2013-12-28 上午11:26:19
 * 
 */
public class BrandJGoods {
	
	/**
	 * 品牌信息
	 */
	private Brand			brand;
	/**
	 * 商品信息集合
	 */
	private List<JGoods>	gs;

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	public List<JGoods> getGs() {
		return gs;
	}

	public void setGs(List<JGoods> gs) {
		this.gs = gs;
	}

}
