
package com.holding.smile.entity;

public class District {
	private Integer	ID;
	private String	disno;
	private String	disname;
	private Integer	dissort;
	private String	remark;
	private Integer	cityid;

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getDisno() {
		return disno;
	}

	public void setDisno(String disno) {
		this.disno = disno;
	}

	public String getDisname() {
		return disname;
	}

	public void setDisname(String disname) {
		this.disname = disname;
	}

	public Integer getDissort() {
		return dissort;
	}

	public void setDissort(Integer dissort) {
		this.dissort = dissort;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getCityid() {
		return cityid;
	}

	public void setCityid(Integer cityid) {
		this.cityid = cityid;
	}

	@Override
	public String toString() {
		// 为什么要重写toString()呢？因为适配器在显示数据的时候，如果传入适配器的对象不是字符串的情况下，直接就使用对象.toString()
		// TODO Auto-generated method stub
		return disname;
	}
}
