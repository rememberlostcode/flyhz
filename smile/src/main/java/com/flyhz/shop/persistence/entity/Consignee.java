
package com.flyhz.shop.persistence.entity;

import java.io.Serializable;

/**
 * The persistent class for the consignee database table.
 * 
 */
public class Consignee implements Serializable {
	private static final long	serialVersionUID	= 1L;

	private int					id;

	private String				address;

	private short				city;

	private short				contury;

	private String				idcard;

	private String				mobilephone;

	private String				name;

	private short				province;

	private int					userId;

	private String				zipcode;

	public Consignee() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public short getCity() {
		return this.city;
	}

	public void setCity(short city) {
		this.city = city;
	}

	public short getContury() {
		return this.contury;
	}

	public void setContury(short contury) {
		this.contury = contury;
	}

	public String getIdcard() {
		return this.idcard;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}

	public String getMobilephone() {
		return this.mobilephone;
	}

	public void setMobilephone(String mobilephone) {
		this.mobilephone = mobilephone;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public short getProvince() {
		return this.province;
	}

	public void setProvince(short province) {
		this.province = province;
	}

	public int getUserId() {
		return this.userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getZipcode() {
		return this.zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

}