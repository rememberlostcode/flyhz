
package com.flyhz.shop.dto;

import java.math.BigDecimal;

public class ProductDto {

	private Integer		id;				// 主键ID

	private String		name;

	private String[]	imgs;

	private BrandDto	brand;

	private String		color;

	private String		brandstyle;

	private BigDecimal	purchasingPrice;

	private short		qty;

	/**
	 * 币种
	 */
	private String				currency;
	/**
	 * 币种符号
	 */
	private String				symbol;

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
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

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getBrandstyle() {
		return brandstyle;
	}

	public void setBrandstyle(String brandstyle) {
		this.brandstyle = brandstyle;
	}

}