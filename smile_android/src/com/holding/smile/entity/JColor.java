
package com.holding.smile.entity;

import java.io.Serializable;

/**
 * 
 * 类说明：商品颜色
 * 
 * @author robin 2014-4-21下午10:03:23
 * 
 */
public class JColor implements Serializable {
	private static final long	serialVersionUID	= 1L;

	/**
	 * 商品ID
	 */
	private Integer				id;
	/**
	 * 颜色名称
	 */
	private String				c;
	/**
	 * 颜色图片路径
	 */
	private String				ci;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getC() {
		return c;
	}

	public void setC(String c) {
		this.c = c;
	}

	public String getCi() {
		return ci;
	}

	public void setCi(String ci) {
		this.ci = ci;
	}

}
