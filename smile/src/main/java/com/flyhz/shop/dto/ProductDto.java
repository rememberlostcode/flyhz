
package com.flyhz.shop.dto;

import java.math.BigDecimal;

public class ProductDto {

	private Integer		id;				// 主键ID

	private String		name;

	private String[]	imgs;

	private BrandDto	brand;

	private BigDecimal	purchasingPrice;

	private short		qty;

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

	public BrandDto getBrand() {
		return brand;
	}

	public void setBrand(BrandDto brand) {
		this.brand = brand;
	}

	public String[] getImgs() {
		return imgs;
	}

	public void setImgs(String[] imgs) {
		this.imgs = imgs;
	}

	public BigDecimal getPurchasingPrice() {
		return purchasingPrice;
	}

	public void setPurchasingPrice(BigDecimal purchasingPrice) {
		this.purchasingPrice = purchasingPrice;
	}

	public short getQty() {
		return qty;
	}

	public void setQty(short qty) {
		this.qty = qty;
	}

}