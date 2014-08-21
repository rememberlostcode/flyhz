
package com.holding.smile.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 折扣Dto
 * 
 * @author robin 2013-12-28 上午11:26:19
 * 
 */
public class DiscountDto implements Serializable {

	private static final long	serialVersionUID	= 1L;
	/**
	 * 折扣ID
	 */
	private Integer				id;
	/**
	 * 折扣
	 */
	private Float				discount;
	/**
	 * 折扣后价格
	 */
	private BigDecimal			dp;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Float getDiscount() {
		return discount;
	}

	public void setDiscount(Float discount) {
		this.discount = discount;
	}

	public BigDecimal getDp() {
		return dp;
	}

	public void setDp(BigDecimal dp) {
		this.dp = dp;
	}

}
