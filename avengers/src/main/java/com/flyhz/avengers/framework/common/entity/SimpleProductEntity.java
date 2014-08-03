
package com.flyhz.avengers.framework.common.entity;

import java.io.Serializable;

public class SimpleProductEntity implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -709467989643153816L;

	private String				id;

	private String				name;

	private String				description;

	private String				brand;

	private String[]			categories;

	private String				styleNo;

	private String				price;

	private String				discountPrice;

	private byte[][]			producImgs;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String[] getCategories() {
		return categories;
	}

	public void setCategories(String[] categories) {
		this.categories = categories;
	}

	public String getStyleNo() {
		return styleNo;
	}

	public void setStyleNo(String styleNo) {
		this.styleNo = styleNo;
	}

	public String getPrice() {
		return price;
	}

	public String getDiscountPrice() {
		return discountPrice;
	}

	public void setDiscountPrice(String discountPrice) {
		this.discountPrice = discountPrice;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public byte[][] getProducImgs() {
		return producImgs;
	}

	public void setProducImgs(byte[][] producImgs) {
		this.producImgs = producImgs;
	}
}
