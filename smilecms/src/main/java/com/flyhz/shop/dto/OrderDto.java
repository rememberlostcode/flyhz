
package com.flyhz.shop.dto;

import java.math.BigDecimal;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

public class OrderDto {

	private Integer					id;

	private String					number;

	private List<OrderDetailDto>	details;

	private ConsigneeDetailDto		consignee;

	private BigDecimal				total;

	private Integer					qty;

	private List<VoucherDto>		vouchers;

	@JsonIgnore
	private UserDto					user;

	private String					time;

	private String					status;

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

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}