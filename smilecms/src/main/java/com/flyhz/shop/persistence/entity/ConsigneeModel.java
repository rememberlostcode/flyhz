
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
	private Short				cityId;
	private Short				conturyId;
	private String				idcard;
	private String				mobilephone;
	private String				name;
	private Short				provinceId;
	private Integer				userId;
	private String				zipcode;
	private Short				districtId;

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

	public Short getCityId() {
		return cityId;
	}

	public void setCityId(Short cityId) {
		this.cityId = cityId;
	}

	public Short getConturyId() {
		return conturyId;
	}

	public void setConturyId(Short conturyId) {
		this.conturyId = conturyId;
	}

	public Short getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(Short provinceId) {
		this.provinceId = provinceId;
	}

	public Short getDistrictId() {
		return districtId;
	}

	public void setDistrictId(Short districtId) {
		this.districtId = districtId;
	}
}