
package com.flyhz.shop.persistence.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.flyhz.shop.dto.ConsigneeDetailDto;
import com.flyhz.shop.persistence.entity.ConsigneeModel;

public interface ConsigneeDao extends GenericDao<ConsigneeModel> {

	public List<ConsigneeDetailDto> getConsigneesByUserId(@Param(value = "id") Integer userId);

	public ConsigneeDetailDto getConsigneeByModel(ConsigneeModel consignee);

	/**
	 * 增加收件人地址
	 * 
	 * @author fuwb 20140402
	 * @param consigneeModel
	 * @return int
	 */
	public int insertConsignee(ConsigneeModel consigneeModel);

	/**
	 * 修改收件人地址
	 * 
	 * @author fuwb 20140402
	 * @param consigneeModel
	 * @return int
	 */
	public int updateConsignee(ConsigneeModel consigneeModel);

	/**
	 * 删除收件人地址
	 * 
	 * @param consigneeModel
	 * @return int
	 */
	public int deleteConsignee(ConsigneeModel consigneeModel);

	/**
	 * 更新收件人地址身份证照片路径
	 * 
	 * @param consigneeModel
	 * @return int
	 */
	public int updateConsigneeIdCard(ConsigneeModel consigneeModel);
}
