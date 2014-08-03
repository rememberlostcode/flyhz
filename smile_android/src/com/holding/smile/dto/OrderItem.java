
package com.holding.smile.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class OrderItem implements Serializable {

	private static final long	serialVersionUID	= 7947163388048463540L;

	private Integer				id;

	private short				qty;

	private ProductDto			product;

	private BigDecimal			logisticsPriceEvery;

	private BigDecimal			logisticsPriceTotal;

	private BigDecimal			total;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public short getQty() {
		return qty;
	}

	public void setQty(short qty) {
		this.qty = qty;
	}

	public ProductDto getProduct() {
		return product;
	}

	public void setProduct(ProductDto product) {
		this.product = product;
	}

	public BigDecimal getLogisticsPriceEvery() {
		return logisticsPriceEvery;
	}

	public void setLogisticsPriceEvery(BigDecimal logisticsPriceEvery) {
		this.logisticsPriceEvery = logisticsPriceEvery;
	}

	public BigDecimal getLogisticsPriceTotal() {
		return logisticsPriceTotal;
	}

	public void setLogisticsPriceTotal(BigDecimal logisticsPriceTotal) {
		this.logisticsPriceTotal = logisticsPriceTotal;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

}