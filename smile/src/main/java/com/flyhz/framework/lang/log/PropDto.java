
package com.flyhz.framework.lang.log;

import java.io.Serializable;

/**
 * 
 * 实体属性Dto
 * 
 * @author robin 2013-08-20
 */
public class PropDto implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 7820466953266346660L;
	private Integer				id;
	private String				name;										// 属性名
	private String				description;								// 描述
	private String				className;									// 类名

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

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
}
