
package com.holding.smile.entity;

import java.io.Serializable;

/**
 * 
 * 类说明：排序类型
 * 
 * @author robin 2014-4-10下午3:03:23
 * 
 */
public class SortType implements Serializable {
	private static final long	serialVersionUID	= 1L;

	/**
	 * 排序名称
	 */
	private String				n;
	/**
	 * 排序类型
	 */
	private String				v;

	public String getN() {
		return n;
	}

	public void setN(String n) {
		this.n = n;
	}

	public String getV() {
		return v;
	}

	public void setV(String v) {
		this.v = v;
	}

}
