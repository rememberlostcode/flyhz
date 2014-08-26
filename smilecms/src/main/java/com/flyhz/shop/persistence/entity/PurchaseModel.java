
package com.flyhz.shop.persistence.entity;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 采购进度
 * 
 * @author Administrator
 */
public class PurchaseModel implements Serializable {
	private static final long	serialVersionUID	= 1L;

	private Integer				id;						//
	private String				orderNumber;				// 海狗订单号
	private Integer				productId;					// 产品ID
	private BigDecimal			reservePrice;				// 预约价格
	private String				tborderId;					// 淘宝订单ID
	private String				logisticsId;				// 物流订单号
	private String				status;					// 购买状态：0--未购买；1--成功；2--失败

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

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public BigDecimal getReservePrice() {
		return reservePrice;
	}

	public void setReservePrice(BigDecimal reservePrice) {
		this.reservePrice = reservePrice;
	}

	public String getTborderId() {
		return tborderId;
	}

	public void setTborderId(String tborderId) {
		this.tborderId = tborderId;
	}

	public String getLogisticsId() {
		return logisticsId;
	}

	public void setLogisticsId(String logisticsId) {
		this.logisticsId = logisticsId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
