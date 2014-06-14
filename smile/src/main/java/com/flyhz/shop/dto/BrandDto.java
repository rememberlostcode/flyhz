
package com.flyhz.shop.dto;

import org.codehaus.jackson.annotate.JsonIgnore;

public class BrandDto {

	private Integer				id;
	private String				name;
	private String				img_url;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonIgnore
	public String getImg_url() {
		return img_url;
	}

	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}

}