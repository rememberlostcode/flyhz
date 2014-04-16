
package com.holding.smile.entity;

public class Province {
	private int		id;
	private String	name;
	private Integer	sort;
	private Integer	conturyid;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Integer getConturyid() {
		return conturyid;
	}

	public void setConturyid(Integer conturyid) {
		this.conturyid = conturyid;
	}

	@Override
	public String toString() {
		// 为什么要重写toString()呢？因为适配器在显示数据的时候，如果传入适配器的对象不是字符串的情况下，直接就使用对象.toString()
		// TODO Auto-generated method stub
		return name;
	}
}
