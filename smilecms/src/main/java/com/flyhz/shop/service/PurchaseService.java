
package com.flyhz.shop.service;

import java.util.List;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.framework.lang.page.Pager;
import com.flyhz.shop.dto.PurchaseDto;
import com.flyhz.shop.dto.PurchasePageDto;

/**
 * 代购进度service
 * 
 * @author Administrator
 */
public interface PurchaseService {
	/**
	 * 分页查询代购进度表
	 * 
	 * @param pager
	 * @param purchaseParam
	 * @return List<PurchaseScheduleDto>
	 */
	public List<PurchaseDto> getPurchaseDtosPage(Pager pager, PurchasePageDto purchaseParam);

	/**
	 * 查询代购记录
	 * 
	 * @param purchaseId
	 * @return PurchaseDto
	 */
	public PurchaseDto getPurchaseDto(Integer purchaseId);

	/**
	 * 编辑代购数据
	 * 
	 * @param userId
	 * @param purchaseDto
	 * @return
	 */
	public void editPurchase(Integer userId, PurchaseDto purchaseDto) throws ValidateException;
}
