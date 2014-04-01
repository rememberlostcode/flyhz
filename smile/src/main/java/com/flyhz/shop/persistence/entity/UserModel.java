
package com.flyhz.shop.persistence.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * The persistent class for the user database table.
 * 
 */

public class UserModel implements Serializable {
	private static final long	serialVersionUID	= 1L;

	private int					id;

	private Date				gmtCreate;

	private Date				gmtModify;

	private Date				gmtRegister;

	private String				identitycard;

	private String				mobilephone;

	private String				password;

	private String				qq;

	private String				username;

	private String				email;

	private String				weibo;

	private String				weixin;

	private String				token;

	public UserModel() {
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

	public Date getGmtRegister() {
		return this.gmtRegister;
	}

	public void setGmtRegister(Date gmtRegister) {
		this.gmtRegister = gmtRegister;
	}

	public String getIdentitycard() {
		return this.identitycard;
	}

	public void setIdentitycard(String identitycard) {
		this.identitycard = identitycard;
	}

	public String getMobilephone() {
		return this.mobilephone;
	}

	public void setMobilephone(String mobilephone) {
		this.mobilephone = mobilephone;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getQq() {
		return this.qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getWeibo() {
		return this.weibo;
	}

	public void setWeibo(String weibo) {
		this.weibo = weibo;
	}

	public String getWeixin() {
		return this.weixin;
	}

	public void setWeixin(String weixin) {
		this.weixin = weixin;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}