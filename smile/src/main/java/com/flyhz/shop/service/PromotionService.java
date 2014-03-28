
package com.flyhz.shop.service;

import java.util.List;

import com.flyhz.shop.dto.Coupon;
import com.flyhz.shop.dto.Voucher;

public interface PromotionService {

	public Coupon getCoupon(Integer userId, Integer couponId);

	public Voucher getVoucher(Integer userId, Integer voucherId);

	public List<Coupon> listCoupons(Integer userId);

	public List<Voucher> listVouchers(Integer userId);

}