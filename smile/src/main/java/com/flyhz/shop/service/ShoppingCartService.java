
package com.flyhz.shop.service;

import java.util.List;

import com.flyhz.shop.dto.Item;

public interface ShoppingCartService {

	public void addItem(Integer userId, Integer productId);

	public void removeItem(Integer userId, Integer itemId);

	public List<Item> listItems(Integer userId);

	public void setQty(Integer userId, Integer itemId, Integer qty);

}