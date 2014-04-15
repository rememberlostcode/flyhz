
package com.holding.smile.entity;

public class City {
	private Integer	ID;
	private String	disno;
	private String	cityname;
	private Integer	citysort;
	private String	remark;
	private Integer	proid;

	public Integer getID() {
		return ID;
	}

	public void setID(Integer iD) {
		ID = iD;
	}

	public String getDisno() {
		return disno;
	}

	public void setDisno(String disno) {
		this.disno = disno;
	}

	public String getCityname() {
		return cityname;
	}

	public void setCityname(String cityname) {
		this.cityname = cityname;
	}

	public Integer getCitysort() {
		return citysort;
	}

	public void setCitysort(Integer citysort) {
		this.citysort = citysort;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getProid() {
		return proid;
	}

	public void setProid(Integer proid) {
		this.proid = proid;
	}

	@Override
	public String toString() {
		// 为什么要重写toString()呢？因为适配器在显示数据的时候，如果传入适配器的对象不是字符串的情况下，直接就使用对象.toString()
		// TODO Auto-generated method stub
		return cityname;
	}
}
