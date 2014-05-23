
package com.flyhz.shop.persistence.dao;

import java.util.List;

import com.flyhz.shop.dto.BrandBuildDto;
import com.flyhz.shop.persistence.entity.BrandModel;

/**
 * 品牌Dao
 * 
 * @author zhangb 2014年4月1日 下午2:37:36
 * 
 */
public interface BrandDao extends GenericDao<BrandModel> {

	/**
	 * 获取所有品牌
	 * @return
	 */
	public List<BrandBuildDto> getBrandBuildDtoList();
}
