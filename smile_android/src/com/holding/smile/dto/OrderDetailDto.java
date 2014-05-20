
package com.holding.smile.dto;

import java.io.Serializable;
import java.util.List;

public class OrderDetailDto extends OrderItem implements Serializable {

	private static final long	serialVersionUID	= -6330260477468777186L;
	private List<CouponDto>		coupons;

	public List<CouponDto> getCoupons() {
		return coupons;
	}

	public void setCoupons(List<CouponDto> coupons) {
		this.coupons = coupons;
	}

}