
package com.flyhz.shop.dto;

import java.math.BigDecimal;
import java.util.List;

public class OrderDto {

	private Integer					id;

	private String					number;

	private List<OrderDetailDto>	details;

	private ConsigneeDetailDto		consignee;

	private BigDecimal				total;

	private List<VoucherDto>		vouchers;

	private UserDto					user;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public List<OrderDetailDto> getDetails() {
		return details;
	}

	public void setDetails(List<OrderDetailDto> details) {
		this.details = details;
	}

	public ConsigneeDetailDto getConsignee() {
		return consignee;
	}

	public void setConsignee(ConsigneeDetailDto consignee) {
		this.consignee = consignee;
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