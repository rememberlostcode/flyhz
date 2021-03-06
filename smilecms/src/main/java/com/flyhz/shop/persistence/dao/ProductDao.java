
package com.flyhz.shop.persistence.dao;

import java.util.List;

import com.flyhz.framework.lang.page.Pager;
import com.flyhz.shop.build.solr.SolrPage;
import com.flyhz.shop.dto.ProductBuildDto;
import com.flyhz.shop.dto.ProductCmsDto;
import com.flyhz.shop.dto.ProductPageDto;
import com.flyhz.shop.persistence.entity.ProductLogModel;
import com.flyhz.shop.persistence.entity.ProductModel;

/**
 * 商品Dao
 * 
 * @author zhangb 2014年4月1日 下午2:37:36
 * 
 */
public interface ProductDao extends GenericDao<ProductModel> {

	public int getCountOfAll();

	public List<ProductBuildDto> findAll(SolrPage page);

	/**
	 * 查询单个商品，用于Solr
	 * 
	 * @param productId
	 * @return ProductBuildDto
	 */
	public ProductBuildDto getProductBuildDtoById(Integer productId);

	/**
	 * 删除产品
	 * 
	 * @param productId
	 * @return int
	 */
	public int deleteProduct(Integer productId);

	/**
	 * 增加产品
	 * 
	 * @param productModel
	 * @return int
	 */
	public int addProduct(ProductModel productModel);

	/**
	 * 编辑产品
	 * 
	 * @param productModel
	 * @return int
	 */
	public int editProduct(ProductModel productModel);

	/**
	 * 分页查询产品列表
	 * 
	 * @param pager
	 * @return list
	 */
	public List<ProductModel> getPagedProducts(Pager pager);

	/**
	 * 分页查询产品DTO列表
	 * 
	 * @param pager
	 * @return list
	 */
	public List<ProductCmsDto> getPageProductCmsDtos(Pager pager);

	/**
	 * 分页查询产品DTO列表
	 * 
	 * @param productPageDto
	 * @return list
	 */
	public List<ProductCmsDto> getProductCmsDtosPage(ProductPageDto productPageDto);

	/**
	 * 根据条件查询产品数量
	 * 
	 * @param productPageDto
	 * @return int
	 */
	public int getProductCmsDtosPageCount(ProductPageDto productPageDto);

	/**
	 * 查询最大自定义款号
	 * 
	 * @param
	 * @return
	 */
	public int getMaxStyle();

	/**
	 * 查询产品数量
	 * 
	 * @param
	 * @return int
	 */
	public int getProductCount();

	/**
	 * 保存产品操作日志
	 * 
	 * @param productLogModel
	 * @return int
	 */
	public int addProductLog(ProductLogModel productLogModel);

	/**
	 * 查询起始ID到结束ID之间的产品
	 * 
	 * @param productPageDto
	 * @return list
	 */
	public List<ProductModel> getProductsByStartAndEnd(ProductPageDto productPageDto);
}
