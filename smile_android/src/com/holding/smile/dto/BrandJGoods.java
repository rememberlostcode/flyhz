
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
	private Integer			i;	// 品牌ID
	private String			n;	// 品牌名
	private List<JGoods>	gs; // 返回数据

	public Integer getI() {
		return i;
	}

	public void setI(Integer i) {
		this.i = i;
	}

	public String getN() {
		return n;
	}

	public void setN(String n) {
		this.n = n;
	}

	public List<JGoods> getGs() {
		return gs;
	}

	public void setGs(List<JGoods> gs) {
		this.gs = gs;
	}

}
