
package com.flyhz.shop.persistence.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * The persistent class for the promotion database table.
 * 
 */

public class PromotionModel implements Serializable {
	private static final long	serialVersionUID	= 1L;

	private int					id;

	private Date				endtime;

	private Date				gmtCreate;

	private Date				gmtModify;

	private BigDecimal			price;

	private int					productId;

	private Date				starttime;

	private String				status;

	public PromotionModel() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getEndtime() {
		return this.endtime;
	}

	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}

	public Date getGmtCreate() {
		return this.gmtCreate;
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

	public BigDecimal getPrice() {
		return this.price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public int getProductId() {
		return this.productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public Date getStarttime() {
		return this.starttime;
	}

	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}