
package com.holding.smile.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 类说明：商品折扣
 * 
 * @author robin 2014-8-19下午10:03:23
 * 
 */
public class JDiscount implements Serializable {
	private static final long	serialVersionUID	= 1L;

	/**
	 * 折扣ID
	 */
	private Integer				id;
	/**
	 * 折扣
	 */
	private float				discount;

	private Integer				userId;
	private Integer				productId;
	private String				status;
	private Date				timeDiscountStart;
	private Date				timeDiscountEnd;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public float getDiscount() {
		return discount;
	}

	public void setDiscount(float discount) {
		this.discount = discount;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getTimeDiscountStart() {
		return timeDiscountStart;
	}

	public void setTimeDiscountStart(Date timeDiscountStart) {
		this.timeDiscountStart = timeDiscountStart;
	}

	public Date getTimeDiscountEnd() {
		return timeDiscountEnd;
	}

	public void setTimeDiscountEnd(Date timeDiscountEnd) {
		this.timeDiscountEnd = timeDiscountEnd;
	}

}
