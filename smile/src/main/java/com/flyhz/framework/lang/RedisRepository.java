
package com.flyhz.framework.lang;

import com.flyhz.shop.dto.ProductDto;

/**
 * redis获取数据
 * 
 * @author zhangb 2014年4月2日 下午6:24:01
 * 
 */
public interface RedisRepository {
	/**
	 * 从redis获得单个物品信息
	 * 
	 * @param productId
	 *            物品ID
	 * @return
	 */
	public ProductDto getProductFromRedis(String productId);
}
