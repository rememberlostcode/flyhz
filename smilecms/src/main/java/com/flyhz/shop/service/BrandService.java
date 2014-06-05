
package com.flyhz.shop.service;

import java.util.List;

import com.flyhz.shop.dto.BrandBuildDto;

/**
 * 品牌Service
 * 
 * @author Administrator
 */
public interface BrandService {
	/**
	 * 查询全部品牌列表
	 * 
	 * @param
	 * @return
	 */
	public List<BrandBuildDto> getAllBrandBuildDtos();
}
