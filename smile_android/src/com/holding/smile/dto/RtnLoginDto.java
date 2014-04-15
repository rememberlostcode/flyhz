package com.holding.smile.dto;

import com.holding.smile.entity.SUser;

/**
 * 登录后返回值对象
 * 
 * @author yangjie 2014-4-15 14:09:19
 * 
 */
public class RtnLoginDto {
	private Integer code; // 系统代码
	private String data; // 返回数据

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
