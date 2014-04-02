
package com.flyhz.shop.service;

import com.flyhz.shop.dto.ProductDto;

/**
 * 商品接口
 * 
 * @author zhangb 2014年4月2日 下午4:19:09
 * 
 */
public interface ProductService {

	/**
	 * 从redis获得单个物品信息
	 * 
	 * @param productId
	 *            物品ID
	 * @return
	 */
	public ProductDto getProductFromRedis(String productId);
}
