
package com.holding.smile.entity;

import java.io.Serializable;

public class UserDto implements Serializable {

	private static final long	serialVersionUID	= 113389749919257070L;

	private Integer				id;

	private String				username;

	private String				token;

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

}