package com.flyhz.framework.lang;

import java.util.Date;
import java.util.List;

import com.flyhz.shop.dto.OrderSimpleDto;
import com.flyhz.shop.dto.ProductBuildDto;

/**
 * SOLR操作接口
 * 
 * @author zhangb 2014年4月4日 下午4:00:15
 * 
 */
public interface SolrData {

	/**
	 * 重新建立订单索引
	 */
	public void reBuildOrder();

	/**
	 * 从solr获得指定品牌和分类的商品，最外层有{}
	 * 
	 * @param cid
	 *            分类ID
	 * @param bid
	 *            品牌ID
	 * @return json格式字符串
	 */
	public String getProductsString(Integer cid, Integer bid);

	/**
	 * 建立/修改单个商品索引
	 * 
	 * @param productBuildDto
	 */
	public void submitProduct(ProductBuildDto productBuildDto);

	/**
	 * 删除单个商品索引
	 * 
	 * @param productId
	 */
	public void removeProduct(String productId);

	/**
	 * 建立商品索引
	 * 
	 * @param productList
	 */
	public void submitProductList(List<ProductBuildDto> productList);

	/**
	 * 建立/修改单个订单索引
	 * 
	 * @param userId
	 * @param orderId
	 * @param status
	 * @param gmtModify
	 */
	public void submitOrder(Integer userId, Integer orderId, String status,
			Date gmtModify);

	/**
	 * 查询指定用户的订单
	 * 
	 * @param userId
	 *            用户ID
	 * @param status
	 *            状态
	 * @return
	 */
	public List<OrderSimpleDto> getOrderIdsFromSolr(Integer userId,
			String status);
}
