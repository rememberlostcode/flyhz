
package com.flyhz.shop.dto;

import java.math.BigDecimal;
import java.util.List;

public class OrderDto {

	private Integer				id;

	private String				details;

	private Character			status;

	private ConsigneeDetailDto	consigneeDetail;

	private BigDecimal			total;

	private List<VoucherDto>	vouchers;

	private UserDto				user;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Character getStatus() {
		return status;
	}

	public void setStatus(Character status) {
		this.status = status;
	}

	public ConsigneeDetailDto getConsigneeDetail() {
		return consigneeDetail;
	}

	public void setConsigneeDetail(ConsigneeDetailDto consigneeDetail) {
		this.consigneeDetail = consigneeDetail;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public List<VoucherDto> getVouchers() {
		return vouchers;
	}

	public void setVouchers(List<VoucherDto> vouchers) {
		this.vouchers = vouchers;
	}

	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto user) {
		this.user = user;
	}

}