
package com.flyhz.shop.persistence.dao;

import java.util.List;

import com.flyhz.framework.solr.PageModel;
import com.flyhz.shop.dto.ProductBuildDto;
import com.flyhz.shop.persistence.entity.ProductModel;

/**
 * 商品Dao
 * 
 * @author zhangb 2014年4月1日 下午2:37:36
 * 
 */
public interface ProductDao extends GenericDao<ProductModel> {

	public int getCountOfAll();

	public List<ProductBuildDto> findAll(PageModel page);
}
