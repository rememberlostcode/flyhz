
package com.flyhz.shop.persistence.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 类说明：商品折扣
 * 
 * @author robin 2014-8-19下午10:03:23
 * 
 */
public class DiscountModel implements Serializable {
	private static final long	serialVersionUID	= 1L;

	/**
	 * 折扣ID
	 */
	private Integer				id;

	private Date				gmtCreate;
	private Date				gmtModify;
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

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModify() {
		return gmtModify;
	}

	public void setGmtModify(Date gmtModify) {
		this.gmtModify = gmtModify;
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
