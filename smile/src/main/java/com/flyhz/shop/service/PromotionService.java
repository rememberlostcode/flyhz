
package com.flyhz.shop.service;

import java.util.List;

import com.flyhz.shop.dto.VoucherDto;
import com.flyhz.shop.persistence.entity.CouponModel;

public interface PromotionService {

	public CouponModel getCoupon(Integer userId, Integer couponId);

	public VoucherDto getVoucher(Integer userId, Integer voucherId);

	public List<CouponModel> listCoupons(Integer userId);

	public List<VoucherDto> listVouchers(Integer userId);

}