
package com.flyhz.shop.persistence.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * The persistent class for the cartitem database table.
 * 
 */
public class Cartitem implements Serializable {
	private static final long	serialVersionUID	= 1L;

	private int					id;

	private Date				gmtCreate;

	private Date				gmtModify;

	private int					productId;

	private byte				qty;

	private int					userId;

	public Cartitem() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
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

	public int getProductId() {
		return this.productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public byte getQty() {
		return this.qty;
	}

	public void setQty(byte qty) {
		this.qty = qty;
	}

	public int getUserId() {
		return this.userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}