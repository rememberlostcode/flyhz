
package com.flyhz.shop.persistence.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * The persistent class for the coupon database table.
 * 
 */
public class Coupon implements Serializable {
	private static final long	serialVersionUID	= 1L;

	private int					id;

	private String				description;

	private Date				gmtCreate;

	private Date				gmtModify;

	private String				name;

	public Coupon() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getGmtCreate() {
		return this.gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModify() {
		return this.gmtModify;
	}

	public void setGmtModify(Date gmtModify) {
		this.gmtModify = gmtModify;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}