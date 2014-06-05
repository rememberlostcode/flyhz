
package com.flyhz.shop.dto;

import com.flyhz.framework.util.Constants;

public class UserDto {

	private Integer	id;

	private String	username;

	private String	token;
	
	private String url = Constants.TB_URL;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}