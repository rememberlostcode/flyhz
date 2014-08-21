
package com.flyhz.shop.persistence.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * The persistent class for the cartitem database table.
 * 
 */
public class CartitemModel implements Serializable {
	private static final long	serialVersionUID	= 1L;
	private Integer				id;
	private Date				gmtCreate;
	private Date				gmtModify;
	private Integer				productId;
	private Byte				qty;
	private Integer				userId;
	private Integer				discountId;

	public CartitemModel() {
	}

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

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Byte getQty() {
		return qty;
	}

	public void setQty(Byte qty) {
		this.qty = qty;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getDiscountId() {
		return discountId;
	}

	public void setDiscountId(Integer discountId) {
		this.discountId = discountId;
	}
}