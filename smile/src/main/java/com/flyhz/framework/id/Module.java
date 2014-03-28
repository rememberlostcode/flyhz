/**
 * 
 */

package com.flyhz.framework.id;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author huoding
 * 
 */
public class Module implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 8811843739209405636L;
	private Integer				id;
	private String				name;
	private String				description;
	private String				methodName;
	private Service				service;
	private String				type;
	private Set<ModuleParam>	paramDtoSet			= new HashSet<ModuleParam>();	// 对应参数

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Set<ModuleParam> getParamDtoSet() {
		return paramDtoSet;
	}

	public void setParamDtoSet(Set<ModuleParam> paramDtoSet) {
		this.paramDtoSet = paramDtoSet;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}
}
