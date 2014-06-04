
package com.flyhz.shop.service;

import java.util.List;

import com.flyhz.shop.dto.CategoryBuildDto;

/**
 * 产品分类Service
 * 
 * @author Administrator
 */
public interface CategoryService {
	/**
	 * 查询
	 * 
	 * @param
	 * @return
	 */
	public List<CategoryBuildDto> getAllCategoryBuildDtos();
}
