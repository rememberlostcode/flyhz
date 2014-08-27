
package com.flyhz.shop.persistence.entity;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 币种实体对象
 * 
 * @author Administrator
 */
public class CurrencyModel implements Serializable {
	private static final long	serialVersionUID	= 1L;
	private String				forShort;					// 币种简写
	private BigDecimal			rate;						// 币种汇率
	private String				symbol;					// 币种符号

	public CurrencyModel() {

	}

	public String getForShort() {
		return forShort;
	}

	public void setForShort(String forShort) {
		this.forShort = forShort;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
}
