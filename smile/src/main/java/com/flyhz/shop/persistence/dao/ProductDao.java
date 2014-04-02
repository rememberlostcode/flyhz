
package com.flyhz.shop.persistence.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.flyhz.shop.build.solr.PageModel;
import com.flyhz.shop.dto.ProductBuildDto;
import com.flyhz.shop.dto.ProductDto;
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

	public ProductDto getProductById(@Param(value = "id") int id);
}
