
package com.flyhz.shop.service;

import java.util.List;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.framework.lang.page.Pager;
import com.flyhz.shop.dto.ProductCmsDto;
import com.flyhz.shop.dto.ProductParamDto;
import com.flyhz.shop.persistence.entity.ProductModel;

/**
 * 商品接口
 * 
 * @author zhangb 2014年4月2日 下午4:19:09
 * 
 */
public interface ProductService {
	/**
	 * 分页查询产品
	 * 
	 * @param pager
	 * @return
	 */
	public List<ProductModel> getProductsByPage(Pager pager);

	/**
	 * 查询产品
	 * 
	 * @param productId
	 * @return
	 * @throws ValidateException
	 */
	public ProductModel getProductById(Integer productId);

	/**
	 * 增加产品
	 * 
	 * @param productParamDto
	 * @throws ValidateException
	 * @return
	 */
	public void addProduct(ProductParamDto productParamDto) throws ValidateException;

	/**
	 * 编辑产品
	 * 
	 * @param productParamDto
	 * @throws ValidateException
	 * @return
	 */
	public void editProduct(ProductParamDto productParamDto) throws ValidateException;

	/**
	 * 删除产品
	 * 
	 * @param productId
	 * @throws ValidateException
	 * @return
	 */
	public void deleteProduct(Integer productId) throws ValidateException;

	/**
	 * 批量删除产品
	 * 
	 * @param productIds
	 * @throws ValidateException
	 * @return
	 */
	public void batchDelProducts(String productIds) throws ValidateException;

	/**
	 * 分页查询产品DTO列表
	 * 
	 * @param pager
	 * @param product
	 * @return list
	 */
	public List<ProductCmsDto> getProductCmsDtosByPage(Pager pager, ProductModel product);
}
