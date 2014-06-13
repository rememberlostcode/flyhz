
package com.holding.smile.dto;

import java.io.Serializable;

/**
 * 
 * 类说明：商品品牌
 * 
 * @author robin 2014-4-21下午3:03:23
 * 
 */
public class BrandDto implements Serializable {
	private static final long	serialVersionUID	= 1L;

	private Integer				id;
	/**
	 * 品牌名称
	 */
	private String				name;

	private String				img_url;

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
