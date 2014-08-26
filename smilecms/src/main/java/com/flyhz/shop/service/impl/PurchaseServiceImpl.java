
package com.flyhz.shop.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.framework.lang.page.Pager;
import com.flyhz.framework.util.JSONUtil;
import com.flyhz.framework.util.StringUtil;
import com.flyhz.shop.dto.PurchaseDto;
import com.flyhz.shop.dto.PurchasePageDto;
import com.flyhz.shop.persistence.dao.PurchaseDao;
import com.flyhz.shop.persistence.entity.PurchaseLogModel;
import com.flyhz.shop.persistence.entity.PurchaseModel;
import com.flyhz.shop.service.PurchaseService;

@Service
public class PurchaseServiceImpl implements PurchaseService {
	@Resource
	private PurchaseDao	purchaseDao;

	@Override
	public List<PurchaseDto> getPurchaseDtosPage(Pager pager, PurchasePageDto purchase) {
		if (pager == null || purchase == null) {
			return null;
		}
		// 定义分页参数
		int currentPage = pager.getCurrentPage();
		int pageSize = pager.getPageSize();
		// 查询产品总数量
		int count = purchaseDao.getPurchasePageCount(purchase);
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
		purchase.setStart((currentPage - 1) * pageSize);
		purchase.setPagesize(pageSize);
		// 查询产品列表
		List<PurchaseDto> purchaseDtos = purchaseDao.getPurchaseDtosPage(purchase);
		if (purchaseDtos != null && !purchaseDtos.isEmpty()) {
			for (PurchaseDto purchaseDto : purchaseDtos) {
				if (StringUtils.isNotBlank(purchaseDto.getCoverSmall())) {
					List<String> appImages = JSONUtil.getJson2EntityList(
							purchaseDto.getCoverSmall(), ArrayList.class, String.class);
					purchaseDto.setAppImages(appImages);
				}
			}
		}
		return purchaseDtos;
	}

	@Override
	public PurchaseDto getPurchaseDto(Integer purchaseId) {
		if (purchaseId != null) {
			PurchaseDto purchaseDto = purchaseDao.getPurchaseDto(purchaseId);
			if (purchaseDto != null && StringUtils.isNotBlank(purchaseDto.getCoverSmall())) {
				List<String> appImages = JSONUtil.getJson2EntityList(purchaseDto.getCoverSmall(),
						ArrayList.class, String.class);
				purchaseDto.setAppImages(appImages);
				return purchaseDto;
			}
		}
		return null;
	}

	@Override
	public void editPurchase(Integer userId, PurchaseDto purchaseDto) throws ValidateException {
		if (userId == null) {
			throw new ValidateException("您尚未登录");
		}
		if (purchaseDto == null || purchaseDto.getId() == null) {
			throw new ValidateException("代购数据不存在");
		}
		if (StringUtil.stringLength(purchaseDto.getLogisticsId()) > 20) {
			throw new ValidateException("物流单号过长");
		}
		PurchaseModel purchaseModel = purchaseDao.getPurchaseModel(purchaseDto.getId());
		if (purchaseModel == null) {
			throw new ValidateException("代购数据不存在");
		}
		// 记录日志
		PurchaseLogModel purchaseLogModel = new PurchaseLogModel();
		purchaseLogModel.setBeforeInfo(JSONUtil.getEntity2Json(purchaseModel));
		purchaseLogModel.setGmtModify(new Date());
		purchaseLogModel.setOperate("edit");
		purchaseLogModel.setUserId(userId);
		purchaseLogModel.setPurchaseId(purchaseDto.getId());
		// 更新代购数据
		purchaseModel.setLogisticsId(StringUtils.isBlank(purchaseDto.getLogisticsId()) ? ""
				: purchaseDto.getLogisticsId());
		purchaseModel.setStatus(purchaseDto.getStatus());
		purchaseDao.updatePurchase(purchaseModel);
		// 日志插入更新后数据
		purchaseLogModel.setAfterInfo(JSONUtil.getEntity2Json(purchaseModel));
		purchaseDao.addPurchaseLog(purchaseLogModel);
	}
}
