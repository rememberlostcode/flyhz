
package com.flyhz.shop.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.framework.lang.page.Pager;
import com.flyhz.shop.dto.RefundPageDto;
import com.flyhz.shop.persistence.dao.RefundDao;
import com.flyhz.shop.persistence.entity.RefundModel;
import com.flyhz.shop.service.RefundService;

@Service
public class RefundServiceImpl implements RefundService {
	@Resource
	private RefundDao	refundDao;

	@Override
	public List<RefundModel> getRefundsPage(Pager pager, RefundPageDto refundPageDto) {
		if (pager == null || refundPageDto == null) {
			return null;
		}
		// 定义分页参数
		int currentPage = pager.getCurrentPage();
		int pageSize = pager.getPageSize();
		// 查询产品总数量
		int count = refundDao.getRefundsPageCount(refundPageDto);
		// pager设置总页数和总数量
		pager.setTotalRowsAmount(count);
		if (count % pageSize == 0) {
			pager.setTotalPages(count / pageSize);
		} else {
			pager.setTotalPages(count / pageSize + 1);
		}
		// 重新定义当前页
		if (currentPage > pager.getTotalPages()) {
			currentPage = 1;
			pager.setCurrentPage(currentPage);
		}
		// 设置分页参数
		refundPageDto.setStart((currentPage - 1) * pageSize);
		refundPageDto.setPagesize(pageSize);
		// 查询产品列表
		List<RefundModel> refunds = refundDao.getRefundsPage(refundPageDto);
		return refunds;
	}

	@Override
	public RefundModel getRefundById(Integer refundId) {
		if (refundId != null) {
			return refundDao.getRefundById(refundId);
		}
		return null;
	}

	@Override
	public void editRefund(Integer userId, RefundModel refund) throws ValidateException {
		if (userId == null) {
			throw new ValidateException("您尚未登录");
		}
		if (refund == null || refund.getId() == null) {
			throw new ValidateException("订单退款数据不存在");
		}
		RefundModel refundCheck = refundDao.getRefundById(refund.getId());
		if (refundCheck == null) {
			throw new ValidateException("订单退款数据不存在");
		}
		// 更新订单退款情况
		refundDao.updateRefund(refund);
	}
}
