
package com.flyhz.shop.persistence.entity;

import java.util.Date;

public class SalesvolumeModel {
	private Integer	id;
	private Date	gmtCreate;
	private Date	gmtModify;
	private Integer	productId;
	private Integer	totalnumber;
	private Date	timeStart;
	private Date	timeEnd;

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

	public Integer getTotalnumber() {
		return totalnumber;
	}

	public void setTotalnumber(Integer totalnumber) {
		this.totalnumber = totalnumber;
	}

	public Date getTimeStart() {
		return timeStart;
	}

	public void setTimeStart(Date timeStart) {
		this.timeStart = timeStart;
	}

	public Date getTimeEnd() {
		return timeEnd;
	}

	public void setTimeEnd(Date timeEnd) {
		this.timeEnd = timeEnd;
	}

}
