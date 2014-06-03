
package com.holding.smile.entity;

import java.io.Serializable;

/**
 * 
 * 类说明：活动商品
 * 
 * @author robin 2014-4-10下午3:03:23
 * 
 */
public class JActivity implements Serializable {
	private static final long	serialVersionUID	= 1L;

	/**
	 * 活动ID
	 */
	private Integer				id;
	/**
	 * 图片路径
	 */
	private String				p;

	/**
	 * 活动页面url
	 */
	private String				url;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getP() {
		return p;
	}

	public void setP(String p) {
		this.p = p;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
