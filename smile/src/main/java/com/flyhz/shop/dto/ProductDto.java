
package com.flyhz.shop.dto;

import java.math.BigDecimal;

public class ProductDto {

	private Integer		id;				// 主键ID

	private String		name;

	private BrandDto	brand;

	private BigDecimal	puarchasingPrice;

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

	public BigDecimal getPuarchasingPrice() {
		return puarchasingPrice;
	}

	public void setPuarchasingPrice(BigDecimal puarchasingPrice) {
		this.puarchasingPrice = puarchasingPrice;
	}
}