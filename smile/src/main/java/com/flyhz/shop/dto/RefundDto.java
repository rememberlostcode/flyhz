
package com.flyhz.shop.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class RefundDto implements Serializable {
	private static final long	serialVersionUID	= -6538358346069114540L;
	private String				tborderId;										// 淘宝订单号
	private BigDecimal			refundFee;										// 退款金额
	private String				refundStatus;									// 可退款状态：0--否
																				// 1--是

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
