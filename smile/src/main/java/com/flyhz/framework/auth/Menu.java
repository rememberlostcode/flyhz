/**
 * 
 */

package com.flyhz.framework.auth;

import java.io.Serializable;
import java.util.List;

import com.flyhz.framework.util.StringUtil;

/**
 * @author huoding
 * 
 */
public class Menu implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -4006895429365675158L;
	private Integer				id;
	private String				name;
	private Integer				parentId;
	private Integer				sorting;
	private String				tip;
	private String				action;
	private List<Menu>			subMenus;
	private Character			type;
	private String				img;
	private Integer				systemId;
	private boolean				isIndex;

	public Menu() {

	}

	/**
	 * @param id
	 * @param name
	 * @param parentId
	 * @param sortting
	 * @param tip
	 * @param action
	 * @param isIndex
	 */
	public Menu(Integer id, String name, Integer parentId, Integer sorting, String tip,
			String action, Character type, String img, boolean isIndex) {
		super();
		this.id = id;
		this.name = name;
		this.parentId = parentId;
		this.sorting = sorting;
		this.tip = tip;
		this.action = action;
		this.type = type;
		this.img = img;
		this.isIndex = isIndex;
	}

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
	 * @return the parentId
	 */
	public Integer getParentId() {
		return parentId;
	}

	/**
	 * @param parentId
	 *            the parentId to set
	 */
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	/**
	 * @return the sorting
	 */
	public Integer getSorting() {
		return sorting;
	}

	/**
	 * @param sorting
	 *            the sorting to set
	 */
	public void setSorting(Integer sorting) {
		this.sorting = sorting;
	}

	/**
	 * @return the tip
	 */
	public String getTip() {
		return tip;
	}

	/**
	 * @param tip
	 *            the tip to set
	 */
	public void setTip(String tip) {
		this.tip = tip;
	}

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action
	 *            the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @return the subMenu
	 */
	public List<Menu> getSubMenus() {
		return subMenus;
	}

	/**
	 * @param subMenu
	 *            the subMenu to set
	 */
	public void setSubMenus(List<Menu> subMenu) {
		this.subMenus = subMenu;
	}

	/**
	 * @return the type
	 */
	public Character getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */

	public void setType(String type) {
		if (StringUtil.isNotEmpty(type)) {
			this.type = new Character(type.charAt(0));
		}
	}

	public void setType(Character type) {
		this.type = type;
	}

	/**
	 * @return the img
	 */
	public String getImg() {
		return img;
	}

	/**
	 * @param img
	 *            the img to set
	 */
	public void setImg(String img) {
		this.img = img;
	}

	public Integer getSystemId() {
		return systemId;
	}

	public void setSystemId(Integer systemId) {
		this.systemId = systemId;
	}

	public boolean isIndex() {
		return isIndex;
	}

	public void setIndex(boolean isIndex) {
		this.isIndex = isIndex;
	}
}
