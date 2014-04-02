
package com.flyhz.shop.service;

import java.util.List;

import com.flyhz.shop.dto.ProductBuildDto;

/**
 * build接口，用于build数据到solr和redis
 * 
 * @author zhangb 2014年4月1日 下午2:20:12
 * 
 */
public interface BuildService {

	/**
	 * build数据到solr和redis
	 */
	public void buildData();

	/**
	 * build数据到solr
	 */
	public void buildSolr();

	/**
	 * build数据到redis
	 */
	public void buildRedis();

	/**
	 * 获取商品总数
	 * 
	 * @return
	 */
	public int getCountOfAll();

	/**
	 * 分页获取商品
	 * 
	 * @param start
	 *            开始序号
	 * @param num
	 *            取得跳数
	 * @return
	 */
	public List<ProductBuildDto> findAll(int start, int num);
}
