
package com.holding.smile.dto;

import java.io.Serializable;

public class VoucherDto implements Serializable {

	private static final long	serialVersionUID	= -5591693379407487935L;

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