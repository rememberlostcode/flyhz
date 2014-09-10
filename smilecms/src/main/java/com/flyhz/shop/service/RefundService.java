
package com.flyhz.shop.service;

import java.util.List;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.framework.lang.page.Pager;
import com.flyhz.shop.dto.RefundPageDto;
import com.flyhz.shop.persistence.entity.RefundModel;

/**
 * 订单退款service
 * 
 * @author Administrator
 */
public interface RefundService {
	/**
	 * 分页查询订单退款情况列表
	 * 
	 * @param pager
	 * @param refundPageDto
	 * @return List<RefundModel>
	 */
	public List<RefundModel> getRefundsPage(Pager pager, RefundPageDto refundPageDto);

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
	 * @param userId
	 * @param refund
	 */
	public void editRefund(Integer userId, RefundModel refund) throws ValidateException;
}
