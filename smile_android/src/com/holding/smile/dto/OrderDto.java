
package com.holding.smile.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.holding.smile.entity.Consignee;
import com.holding.smile.entity.UserDto;

/**
 * 
 * 类说明：订单信息
 * 
 * @author robin 2014-4-22下午3:23:51
 * 
 */
public class OrderDto implements Serializable {

	private static final long		serialVersionUID	= 4145258738736071421L;

	private Integer					id;

	private String					number;

	private List<OrderDetailDto>	details;

	private Consignee				consignee;

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

	public Consignee getConsignee() {
		return consignee;
	}

	public void setConsignee(Consignee consignee) {
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