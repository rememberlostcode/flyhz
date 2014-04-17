
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
	private Integer	areacode;
	private Integer	telephone;
	private String	mobilephone;
	private UserDto	user;
	private IdentitycardDto identitycard;

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

	public Integer getAreacode() {
		return areacode;
	}

	public void setAreacode(Integer areacode) {
		this.areacode = areacode;
	}

	public Integer getTelephone() {
		return telephone;
	}

	public void setTelephone(Integer telephone) {
		this.telephone = telephone;
	}

	public String getMobilephone() {
		return mobilephone;
	}

	public void setMobilephone(String mobilephone) {
		this.mobilephone = mobilephone;
	}

	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto user) {
		this.user = user;
	}

	public IdentitycardDto getIdentitycard() {
		return identitycard;
	}

	public void setIdentitycard(IdentitycardDto identitycard) {
		this.identitycard = identitycard;
	}
	
}