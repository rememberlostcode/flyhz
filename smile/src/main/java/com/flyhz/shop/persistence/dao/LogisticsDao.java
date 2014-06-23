
package com.flyhz.shop.persistence.dao;

import com.flyhz.shop.dto.LogisticsDto;
import com.flyhz.shop.persistence.entity.LogisticsModel;

/**
 * 物流
 * 
 * @author zhangb
 * 
 */
public interface LogisticsDao extends GenericDao<LogisticsModel> {

	/**
	 * 通过smile订单号获取mysql中已有的物流信息
	 * 
	 * @param number
	 * @return
	 */
	public LogisticsModel getLogisticsByOrderNumber(String number);

	/**
	 * 插入物流信息
	 * 
	 * @param logisticsDto
	 */
	public void insertLogistics(LogisticsDto logisticsDto);

	/**
	 * 更新物流信息
	 * 
	 * @param logisticsDto
	 */
	public void updateLogistics(LogisticsDto logisticsDto);
}
