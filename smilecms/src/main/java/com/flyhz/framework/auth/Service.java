/**
 * 
 */

package com.flyhz.framework.auth;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author robin
 * 
 */
public class Service implements Serializable {

	private static final long	serialVersionUID	= 8811843739209405636L;
	private Integer				id;
	private String				serviceName;
	private String				description;
	private Integer				systemId;										// 系统ID
	private Set<Module>			methodSet			= new HashSet<Module>();	// 对应方法

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
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

	public Integer getSystemId() {
		return systemId;
	}

	public void setSystemId(Integer systemId) {
		this.systemId = systemId;
	}

	public Set<Module> getMethodSet() {
		return methodSet;
	}

	public void setMethodSet(Set<Module> methodSet) {
		this.methodSet = methodSet;
	}
}
