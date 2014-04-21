
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
	private String				color;
	/**
	 * 颜色图片路径
	 */
	private String				colorimg;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getColorimg() {
		return colorimg;
	}

	public void setColorimg(String colorimg) {
		this.colorimg = colorimg;
	}

}
