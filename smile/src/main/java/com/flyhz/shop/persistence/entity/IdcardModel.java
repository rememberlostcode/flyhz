package com.flyhz.shop.persistence.entity;

import java.io.Serializable;

public class IdcardModel implements Serializable{

	private static final long	serialVersionUID	= 1335899818035738020L;
	private Integer				id;
	private String				name;
	private String				idcard;
	private String				photo;
	private Integer				userId;
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
	public String getIdcard() {
		return idcard;
	}
	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
}
