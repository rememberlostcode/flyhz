
package com.flyhz.shop.persistence.dao;

import java.util.List;

import com.flyhz.shop.dto.PurchaseDto;
import com.flyhz.shop.dto.PurchasePageDto;
import com.flyhz.shop.persistence.entity.PurchaseLogModel;
import com.flyhz.shop.persistence.entity.PurchaseModel;

public interface PurchaseDao extends GenericDao<PurchaseModel> {
	/**
	 * 获取代购进度count
	 * 
	 * @param
	 * @return int
	 */
	public int getPurchasePageCount(PurchasePageDto purchasePageDto);

	/**
	 * 分页查询代购进度
	 * 
	 * @param purchasePageDto
	 * @return List<PurchaseDto>
	 */
	public List<PurchaseDto> getPurchaseDtosPage(PurchasePageDto purchasePageDto);

	/**
	 * 查询代购记录
	 * 
	 * @param purchaseId
	 * @return PurchaseDto
	 */
	public PurchaseDto getPurchaseDto(Integer purchaseId);

	/**
	 * 查询代购记录
	 * 
	 * @param purchaseId
	 * @return PurchaseModel
	 */
	public PurchaseModel getPurchaseModel(Integer purchaseId);

	/**
	 * 更新代购记录
	 * 
	 * @param purchaseModel
	 * @return int
	 */
	public int updatePurchase(PurchaseModel purchaseModel);

	/**
	 * 插入代购操作日志
	 * 
	 * @param purchaseLogModel
	 * @return int
	 */
	public int addPurchaseLog(PurchaseLogModel purchaseLogModel);
}
