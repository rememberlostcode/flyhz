
package com.flyhz.shop.persistence.dao;

import java.util.List;

import com.flyhz.shop.dto.CategoryBuildDto;
import com.flyhz.shop.persistence.entity.CategoryModel;

/**
 * 分类Dao
 * 
 * @author zhangb 2014年4月1日 下午2:37:36
 * 
 */
public interface CategoryDao extends GenericDao<CategoryModel> {

	public List<CategoryBuildDto> getCategoryBuildDtoList();
}
