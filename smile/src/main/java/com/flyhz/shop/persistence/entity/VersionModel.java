
package com.flyhz.shop.persistence.entity;

import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;

public class VersionModel implements Serializable {

	private static final long	serialVersionUID	= 1L;
	@JsonIgnore
	private Integer				id;
	@JsonIgnore
	private Date				gmtCreate;
	@JsonIgnore
	private Date				gmtModify;
	
	@JsonIgnore
	private String				versionNow;
	private String				versionNew;
	private String				versionApk;
	private String				versionLog;
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
	public String getVersionNow() {
		return versionNow;
	}
	public void setVersionNow(String versionNow) {
		this.versionNow = versionNow;
	}
	public String getVersionNew() {
		return versionNew;
	}
	public void setVersionNew(String versionNew) {
		this.versionNew = versionNew;
	}
	public String getVersionApk() {
		return versionApk;
	}
	public void setVersionApk(String versionApk) {
		this.versionApk = versionApk;
	}
	public String getVersionLog() {
		return versionLog;
	}
	public void setVersionLog(String versionLog) {
		this.versionLog = versionLog;
	}

}
