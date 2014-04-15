
package com.holding.smile.entity;

public class Province {
	private int		ID;
	private String	disno;
	private String	proname;
	private Integer	prosort;
	private String	remark;

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

	public String getProname() {
		return proname;
	}

	public void setProname(String proname) {
		this.proname = proname;
	}

	public Integer getProsort() {
		return prosort;
	}

	public void setProsort(Integer prosort) {
		this.prosort = prosort;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		// 为什么要重写toString()呢？因为适配器在显示数据的时候，如果传入适配器的对象不是字符串的情况下，直接就使用对象.toString()
		// TODO Auto-generated method stub
		return proname;
	}
}
