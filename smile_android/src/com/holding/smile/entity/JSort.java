
package com.holding.smile.entity;

import java.io.Serializable;

/**
 * 
 * 类说明：商品分类
 * 
 * @author robin 2014-4-10下午3:03:23
 * 
 */
public class JSort implements Serializable {
	private static final long	serialVersionUID	= 1L;

	/**
	 * 排行名称
	 */
	private String				n;
	/**
	 * 排行链接
	 */
	private String				u;

	public String getN() {
		return n;
	}

	public void setN(String n) {
		this.n = n;
	}

	public String getU() {
		return u;
	}

	public void setU(String u) {
		this.u = u;
	}

}
