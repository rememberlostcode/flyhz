
package com.flyhz.shop.dto;

/**
 * 订单退款参数DTO
 * 
 * @author Administrator
 */
public class RefundPageDto {
	private Integer	start;			// 分页开始位置
	private Integer	end;			// 分页结束位置
	private Integer	pagesize;		// 每页产品数量
	private Integer	orderNumber;	// 海狗订单号
	private String	tborderId;		// 淘宝订单号
	private String	refundStatus;	// 可退款状态：0--否 1--是

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getEnd() {
		return end;
	}

	public void setEnd(Integer end) {
		this.end = end;
	}

	public Integer getPagesize() {
		return pagesize;
	}

	public void setPagesize(Integer pagesize) {
		this.pagesize = pagesize;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getTborderId() {
		return tborderId;
	}

	public void setTborderId(String tborderId) {
		this.tborderId = tborderId;
	}

	public String getRefundStatus() {
		return refundStatus;
	}

	public void setRefundStatus(String refundStatus) {
		this.refundStatus = refundStatus;
	}
}
