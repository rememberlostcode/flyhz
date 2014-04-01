
package com.flyhz.shop.build.solr;

/**
 * 商品计算分数工具接口
 * 
 * @author zhangb 2014年4月1日 下午1:24:19
 * 
 */
public interface ProductFraction {

	/**
	 * 获取商品的分数
	 * 
	 * @param fraction
	 * @return
	 */
	public Double getProductFraction(Fraction fraction);

}
