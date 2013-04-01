
package com.flyhz.shop.persistence.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * The persistent class for the product database table.
 * 
 */

public class ProductModel implements Serializable {
	private static final long	serialVersionUID	= 1L;

	private int					id;

	private int					brandId;

	private String				brandstyle;

	private int					categoryId;

	private String				description;

	private Date				gmtCreate;

	private Date				gmtModify;

	private String				imgs;

	private BigDecimal			localprice;

	private String				name;

	private BigDecimal			purchasingprice;

	private String				style;

	public ProductModel() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBrandId() {
		return this.brandId;
	}

	public void setBrandId(int brandId) {
		this.brandId = brandId;
	}

	public String getBrandstyle() {
		return this.brandstyle;
	}

	public void setBrandstyle(String brandstyle) {
		this.brandstyle = brandstyle;
	}

	public int getCategoryId() {
		return this.categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
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

	public String getImgs() {
		return this.imgs;
	}

	public void setImgs(String imgs) {
		this.imgs = imgs;
	}

	public BigDecimal getLocalprice() {
		return this.localprice;
	}

	public void setLocalprice(BigDecimal localprice) {
		this.localprice = localprice;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPurchasingprice() {
		return this.purchasingprice;
	}

	public void setPurchasingprice(BigDecimal purchasingprice) {
		this.purchasingprice = purchasingprice;
	}

	public String getStyle() {
		return this.style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

}