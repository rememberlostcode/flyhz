
package com.holding.smile.entity;

import java.io.Serializable;

/**
 * 身份证
 * 
 * @author silvermoon
 * 
 */
public class Idcard implements Serializable {

	private static final long	serialVersionUID	= -6318242080957337047L;

	private Integer				id;
	private String				name;
	private String				number;
	private String				url;
	private String				back_url;
	private Integer				userId;
	private String				gmtCreate;
	private String				gmtModify;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBack_url() {
		return back_url;
	}

	public void setBack_url(String back_url) {
		this.back_url = back_url;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(String gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public String getGmtModify() {
		return gmtModify;
	}

	public void setGmtModify(String gmtModify) {
		this.gmtModify = gmtModify;
	}

}