package com.holding.smile.entity;

import java.io.Serializable;

/**
 * 品牌
 * @author zhangb
 *
 */
public class Brand implements Serializable {

	private static final long	serialVersionUID	= 7726637667817705868L;
	private Integer			id; // 品牌ID
	private String			name;	// 品牌名
	private String			img_url;	// 品牌图片url
	
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

	public String getImg_url() {
		return img_url;
	}

	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}

}
