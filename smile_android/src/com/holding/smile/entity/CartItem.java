
package com.holding.smile.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import com.holding.smile.dto.DiscountDto;
import com.holding.smile.dto.ProductDto;

public class CartItem implements Serializable {

	private static final long	serialVersionUID	= 1L;

	private Integer				id;

	private Short				qty;

	private ProductDto			product;

	private BigDecimal			total;

	private DiscountDto			discount;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Short getQty() {
		return qty;
	}

	public void setQty(Short qty) {
		this.qty = qty;
	}

	public ProductDto getProduct() {
		return product;
	}

	public void setProduct(ProductDto product) {
		this.product = product;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public DiscountDto getDiscount() {
		return discount;
	}

	public void setDiscount(DiscountDto discount) {
		this.discount = discount;
	}

}