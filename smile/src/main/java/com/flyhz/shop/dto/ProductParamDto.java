
package com.flyhz.shop.dto;

import java.math.BigDecimal;

public class ProductParamDto {

	private Integer		id;				// 主键ID

	private String		name;

	private Integer		brandId;

	private String		brandName;

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

	public Integer getBrandId() {
		return brandId;
	}

	public void setBrandId(Integer brandId) {
		this.brandId = brandId;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public BigDecimal getPuarchasingPrice() {
		return puarchasingPrice;
	}

	public void setPuarchasingPrice(BigDecimal puarchasingPrice) {
		this.puarchasingPrice = puarchasingPrice;
	}
}