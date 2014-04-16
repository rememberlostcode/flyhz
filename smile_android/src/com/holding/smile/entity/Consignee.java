
package com.holding.smile.entity;

/**
 * 联系人
 * 
 * @author zhangb 2014年4月16日 上午10:21:31
 * 
 */
public class Consignee {
	private Integer	id;
	private String	name;
	private Integer	conturyId;
	private Integer	provinceId;
	private Integer	cityId;
	private Integer	districtId;
	private String	address;
	private String	zipcode;
	private String	mobilephone;
	private String	idcard;
	private Integer	userId;

	public Consignee() {
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

	public Integer getConturyId() {
		return conturyId;
	}

	public void setConturyId(Integer conturyId) {
		this.conturyId = conturyId;
	}

	public Integer getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public Integer getDistrictId() {
		return districtId;
	}

	public void setDistrictId(Integer districtId) {
		this.districtId = districtId;
	}

}