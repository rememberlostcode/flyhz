
package com.holding.smile.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单支付状态
 * 
 * @author fuwb
 */
public class OrderPayDto implements Serializable {
	private static final long	serialVersionUID	= -2178213128921922L;

	private String				number;
	private String				time;
	private String				status;
	private BigDecimal			total;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}
}
