
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
	private Integer				id;
	private Integer				brandId;
	private String				brandstyle;
	private Integer				categoryId;
	private String				description;
	private Date				gmtCreate;
	private Date				gmtModify;
	private String				imgs;
	private BigDecimal			localprice;
	private String				name;
	private BigDecimal			purchasingprice;
	private String				style;
	private String				color;
	private String				colorimg;

	public ProductModel() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getBrandId() {
		return brandId;
	}

	public void setBrandId(Integer brandId) {
		this.brandId = brandId;
	}

	public String getBrandstyle() {
		return brandstyle;
	}

	public void setBrandstyle(String brandstyle) {
		this.brandstyle = brandstyle;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModify() {
		return gmtModify;
	}

	public void setGmtModify(Date gmtModify) {
		this.gmtModify = gmtModify;
	}

	public String getImgs() {
		return imgs;
	}

	public void setImgs(String imgs) {
		this.imgs = imgs;
	}

	public BigDecimal getLocalprice() {
		return localprice;
	}

	public void setLocalprice(BigDecimal localprice) {
		this.localprice = localprice;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPurchasingprice() {
		return purchasingprice;
	}

	public void setPurchasingprice(BigDecimal purchasingprice) {
		this.purchasingprice = purchasingprice;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
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