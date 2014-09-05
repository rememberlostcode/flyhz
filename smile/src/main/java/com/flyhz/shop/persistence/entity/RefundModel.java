
package com.flyhz.shop.persistence.entity;

import java.math.BigDecimal;

/**
 * 订单退款Model
 * 
 * @author Administrator
 */
public class RefundModel {
	private Integer		id;			// 主键ID
	private String		orderNumber;	// 海狗订单号
	private String		tborderId;		// 淘宝订单号
	private BigDecimal	refundFee;		// 退款金额
	private String		refundStatus;	// 可退款状态：0--否 1--是

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getTborderId() {
		return tborderId;
	}

	public void setTborderId(String tborderId) {
		this.tborderId = tborderId;
	}

	public BigDecimal getRefundFee() {
		return refundFee;
	}

	public void setRefundFee(BigDecimal refundFee) {
		this.refundFee = refundFee;
	}

	public String getRefundStatus() {
		return refundStatus;
	}

	public void setRefundStatus(String refundStatus) {
		this.refundStatus = refundStatus;
	}
}
