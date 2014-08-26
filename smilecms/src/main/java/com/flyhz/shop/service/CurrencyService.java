
package com.flyhz.shop.service;

import java.util.List;

/**
 * 币种service
 * 
 * @author Administrator
 */
public interface CurrencyService {
	/**
	 * 查询全部币种简写
	 * 
	 * @param
	 * @return List<String>
	 */
	public List<String> getCurrencyShorts();
}
