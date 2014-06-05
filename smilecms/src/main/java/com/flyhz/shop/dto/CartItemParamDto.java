
package com.flyhz.shop.dto;


/**
 * 
 * 类说明：查询传参时用
 * 
 * @author robin 2014-4-3上午10:43:12
 * 
 */
public class CartItemParamDto {

	private Integer		userId;

	private Integer[]	itemIds;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer[] getItemIds() {
		return itemIds;
	}

	public void setItemIds(Integer[] itemIds) {
		this.itemIds = itemIds;
	}

}