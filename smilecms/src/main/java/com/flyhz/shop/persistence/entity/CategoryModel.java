
package com.flyhz.shop.persistence.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * The persistent class for the category database table.
 * 
 */
public class CategoryModel implements Serializable {
	private static final long	serialVersionUID	= 1L;
	private Integer				id;
	private Date				gmtCreate;
	private Date				gmtModify;
	private String				name;

	public CategoryModel() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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