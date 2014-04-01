
package com.flyhz.shop.dto;

import java.math.BigDecimal;

public class Product {

	private Integer		id;				// 主键ID

	private String		name;

	private Brand		brand;

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

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	public BigDecimal getPuarchasingPrice() {
		return puarchasingPrice;
	}

	public void setPuarchasingPrice(BigDecimal puarchasingPrice) {
		this.puarchasingPrice = puarchasingPrice;
	}
}