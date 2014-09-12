
package com.flyhz.shop.persistence.dao;

import com.flyhz.shop.persistence.entity.RefundModel;

/**
 * 订单退款DAO
 * 
 * @author zb
 */
public interface RefundDao extends GenericDao<RefundModel> {
	
	public RefundModel getRefundByOrderNumber(String orderNumber);
}
