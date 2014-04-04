
package com.flyhz.shop.service;


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
	 * 获取当前美元汇率
	 * 
	 * @return
	 */
	public double getDollarExchangeRate();
}
