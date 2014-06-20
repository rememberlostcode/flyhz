
package com.flyhz.shop.persistence.dao;

import com.flyhz.shop.dto.LogisticsDto;
import com.flyhz.shop.persistence.entity.LogisticsModel;

/**
 * 物流
 * @author zhangb
 *
 */
public interface LogisticsDao extends GenericDao<LogisticsModel> {

	public LogisticsModel getLogisticsByOrderId(Integer orderId);

	public void insertLogistics(LogisticsDto logisticsDto);
	
	public void updateLogistics(LogisticsDto logisticsDto);
}
