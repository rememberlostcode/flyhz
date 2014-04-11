
package com.holding.smile.entity;

import java.io.Serializable;

/**
 * 
 * 类说明：商品分类
 * 
 * @author robin 2014-4-10下午3:03:23
 * 
 */
public class Category implements Serializable {
	private static final long	serialVersionUID	= 1L;

	/**
	 * 分类ID
	 */
	private Integer				id;
	/**
	 * 分类名称
	 */
	private String				name;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
