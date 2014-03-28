/**
 * 
 */

package com.flyhz.framework.id;

import java.io.Serializable;
import java.util.List;


/**
 * @author huoding
 * 
 */
public class ManagementSystem implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 8093794615839818253L;

	private Integer				id;

	private String				name;

	private List<Menu>			menus;

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
	 * @return the menus
	 */
	public List<Menu> getMenus() {
		return menus;
	}

	/**
	 * @param menus
	 *            the menus to set
	 */
	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}

}
