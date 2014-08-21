
package com.flyhz.shop.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.flyhz.shop.persistence.entity.DiscountModel;

/**
 * 
 * 类说明：折扣Dto
 * 
 * @author robin 2014-8-21上午11:23:40
 * 
 */
public class DiscountDto implements Serializable {

	private static final long	serialVersionUID	= 1L;
	/**
	 * 折扣信息
	 */
	private DiscountModel		discount;
	/**
	 * 折扣后价格
	 */
	private BigDecimal			dp;

	public DiscountModel getDiscount() {
		return discount;
	}

	public void setDiscount(DiscountModel discount) {
		this.discount = discount;
	}

	public BigDecimal getDp() {
		return dp;
	}

	public void setDp(BigDecimal dp) {
		this.dp = dp;
	}

}
