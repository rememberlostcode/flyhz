
package com.holding.smile.dto;

import java.util.List;

public class OrderDetailDto extends OrderItem {

	private List<CouponDto>	coupons;

	public List<CouponDto> getCoupons() {
		return coupons;
	}

	public void setCoupons(List<CouponDto> coupons) {
		this.coupons = coupons;
	}

}