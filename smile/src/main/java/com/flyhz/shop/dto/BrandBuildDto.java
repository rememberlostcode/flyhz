
package com.flyhz.shop.dto;

import java.io.Serializable;

/**
 * 用于build的品牌dto
 * 
 * @author zhangb 2014年4月3日 下午3:48:41
 * 
 */
public class BrandBuildDto implements Serializable {
	private static final long	serialVersionUID	= 1L;
	private Integer				id;
	private String				name;
	private String				img_url;

	public BrandBuildDto() {
	}

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

	public String getImg_url() {
		return img_url;
	}

	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}

}