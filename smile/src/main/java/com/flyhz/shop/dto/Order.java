
package com.flyhz.shop.dto;

import java.math.BigDecimal;
import java.util.List;

public class Order {

	private Integer			id;

	private String			details;

	private Character		status;

	private ConsigneeDetail	consigneeDetail;

	private BigDecimal		total;

	private List<Voucher>	vouchers;

	private User			user;

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

	public ConsigneeDetail getConsigneeDetail() {
		return consigneeDetail;
	}

	public void setConsigneeDetail(ConsigneeDetail consigneeDetail) {
		this.consigneeDetail = consigneeDetail;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public List<Voucher> getVouchers() {
		return vouchers;
	}

	public void setVouchers(List<Voucher> vouchers) {
		this.vouchers = vouchers;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}