
package com.flyhz.framework.auth;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * 实体Dto
 * 
 * @author robin 2013-08-20
 */
public class EntityDto implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -1549128800426554150L;
	private Integer				id;
	private String				name;											// 实体类名
	private String				description;									// 描述
	private Set<PropDto>		props				= new HashSet<PropDto>();	// 实体属性

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

	public Set<PropDto> getProps() {
		return props;
	}

	public void setProps(Set<PropDto> props) {
		this.props = props;
	}

}
