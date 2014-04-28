
package com.holding.smile.dto;

import java.io.Serializable;

public class CouponDto implements Serializable {

	private static final long	serialVersionUID	= -1046689045939187548L;

	private Integer				id;

	private String				name;

	private String				desc;

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

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}