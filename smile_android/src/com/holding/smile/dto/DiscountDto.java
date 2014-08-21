
package com.holding.smile.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.holding.smile.entity.JDiscount;

/**
 * 折扣Dto
 * 
 * @author robin 2013-12-28 上午11:26:19
 * 
 */
public class DiscountDto implements Serializable {

	private static final long	serialVersionUID	= 1L;
	/**
	 * 折扣信息
	 */
	private JDiscount			discount;
	/**
	 * 折扣后价格
	 */
	private BigDecimal			dp;

	public JDiscount getDiscount() {
		return discount;
	}

	public void setDiscount(JDiscount discount) {
		this.discount = discount;
	}

	public BigDecimal getDp() {
		return dp;
	}

	public void setDp(BigDecimal dp) {
		this.dp = dp;
	}

}
