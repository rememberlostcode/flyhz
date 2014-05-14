
package com.holding.smile.entity;

import java.io.Serializable;

/**
 * 联系人
 * 
 * @author zhangb 2014年4月16日 上午10:21:31
 * 
 */
public class Consignee implements Serializable {
	private static final long	serialVersionUID	= 3730923451209957486L;
	private Integer				id;
	private String				name;
	private String				address;
	private String				zipcode;
	private String				phone;
	private UserDto				user;
	private Idcard				identitycard;
	private boolean				isDel;

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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setDel(boolean isDel) {
		this.isDel = isDel;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto user) {
		this.user = user;
	}

	public Idcard getIdentitycard() {
		return identitycard;
	}

	public void setIdentitycard(Idcard identitycard) {
		this.identitycard = identitycard;
	}

	public boolean getIsDel() {
		return isDel;
	}

	public void setIsDel(boolean isDel) {
		this.isDel = isDel;
	}

}