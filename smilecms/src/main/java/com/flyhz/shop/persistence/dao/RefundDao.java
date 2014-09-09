
package com.flyhz.shop.persistence.dao;

import java.util.List;

import com.flyhz.shop.dto.RefundPageDto;
import com.flyhz.shop.persistence.entity.RefundModel;

/**
 * 订单退款DAO
 * 
 * @author Administrator
 */
public interface RefundDao extends GenericDao<RefundModel> {
	/**
	 * 查询订单退款情况总数量
	 * 
	 * @param refundPageDto
	 * @return int
	 */
	public int getRefundsPageCount(RefundPageDto refundPageDto);

	/**
	 * 分页查询订单退款情况列表
	 * 
	 * @param refundPageDto
	 * @return List<RefundModel>
	 */
	public List<RefundModel> getRefundsPage(RefundPageDto refundPageDto);

	/**
	 * 根据ID查询订单退款情况
	 * 
	 * @param refundId
	 * @return RefundModel
	 */
	public RefundModel getRefundById(Integer refundId);

	/**
	 * 编辑订单退款情况
	 * 
	 * @param refundModel
	 * @return int
	 */
	public int updateRefund(RefundModel refundModel);
}
