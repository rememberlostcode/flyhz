
package com.flyhz.shop.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * 类说明：订单支付Dto
 * 
 * @author robin 2014-4-3下午12:10:40
 * 
 */
public class OrderPayDto {
	private Integer		id;
	private String		number;
	private Date		gmtCreate;
	private Date		gmtModify;
	private Character	status;
	private BigDecimal	total;
	private Integer		userId;

	public OrderPayDto() {
	}

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

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModify() {
		return this.gmtModify;
	}

	public void setGmtModify(Date gmtModify) {
		this.gmtModify = gmtModify;
	}

	public Character getStatus() {
		return status;
	}

	public void setStatus(Character status) {
		this.status = status;
	}

	public BigDecimal getTotal() {
		return this.total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

}