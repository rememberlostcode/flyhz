
package com.flyhz.shop.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * 类说明：订单确认后返回的对象
 * 
 * @author robin 2014-4-3下午12:10:40
 * 
 */
public class RtnOrderDto {
	private String		number;
	private Date		time;
	private BigDecimal	total;

	public RtnOrderDto() {
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

}