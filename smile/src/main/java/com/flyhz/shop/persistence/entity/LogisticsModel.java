
package com.flyhz.shop.persistence.entity;

import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;

public class LogisticsModel implements Serializable {
	private static final long	serialVersionUID	= 5301439009751675761L;
	@JsonIgnore
	protected Integer			id;
	@JsonIgnore
	protected String			number;
	@JsonIgnore
	protected String			content;

	@JsonIgnore
	protected Date				gmtCreate;
	@JsonIgnore
	protected Date				gmtModify;
	/**
	 * 快递公司
	 */
	protected String			companyName;
	/**
	 * 物流状态描述
	 */
	protected String			logisticsStatus;
	/**
	 * 运单号
	 */
	protected Long				tid;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getLogisticsStatus() {
		return logisticsStatus;
	}

	public void setLogisticsStatus(String logisticsStatus) {
		this.logisticsStatus = logisticsStatus;
	}

	public Long getTid() {
		return tid;
	}

	public void setTid(Long tid) {
		this.tid = tid;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
}
