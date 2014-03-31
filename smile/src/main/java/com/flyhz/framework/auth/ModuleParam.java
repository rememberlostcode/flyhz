
package com.flyhz.framework.auth;

import java.io.Serializable;

/**
 * 
 * 模块参数Dto
 * 
 * @author robin 2013-08-20
 */
public class ModuleParam implements Serializable {
	private static final long	serialVersionUID	= 1L;

	private Integer				id;
	private String				name;						// 参数名
	private String				description;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
