
package com.flyhz.shop.persistence.entity;

import java.io.Serializable;

/**
 * The persistent class for the consignee database table.
 * 
 */
public class ConsigneeModel implements Serializable {
	private static final long	serialVersionUID	= 1L;
	private Integer				id;
	private String				address;
	private Short				city;
	private Short				contury;
	private String				idcard;
	private String				mobilephone;
	private String				name;
	private Short				province;
	private Integer				userId;
	private String				zipcode;
	private Short				town;

	public ConsigneeModel() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Short getCity() {
		return city;
	}

	public void setCity(Short city) {
		this.city = city;
	}

	public Short getContury() {
		return contury;
	}

	public void setContury(Short contury) {
		this.contury = contury;
	}

	public String getIdcard() {
		return idcard;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}

	public String getMobilephone() {
		return mobilephone;
	}

	public void setMobilephone(String mobilephone) {
		this.mobilephone = mobilephone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Short getProvince() {
		return province;
	}

	public void setProvince(Short province) {
		this.province = province;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public Short getTown() {
		return town;
	}

	public void setTown(Short town) {
		this.town = town;
	}
}