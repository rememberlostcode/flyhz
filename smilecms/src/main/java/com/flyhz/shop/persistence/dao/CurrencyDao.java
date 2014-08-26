
package com.flyhz.shop.persistence.dao;

import java.util.List;

import com.flyhz.shop.persistence.entity.CurrencyModel;

/**
 * 币种DAO
 * 
 * @author Administrator
 */
public interface CurrencyDao extends GenericDao<CurrencyModel> {
	/**
	 * 查询全部币种简写
	 * 
	 * @param
	 * @return List<String>
	 */
	public List<String> getCurrencyShorts();
}
