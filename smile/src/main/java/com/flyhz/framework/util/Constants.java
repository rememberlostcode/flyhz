
package com.flyhz.framework.util;

public class Constants {

	/**
	 * solr搜索链接
	 */
	public static final String	SEARCH_URL			= "/solr/common/select";
	public static final String	IMAGES_URL			= "/static";

	/**
	 * solr搜索结果的需要返回的物品属性
	 */
	public static final String	PARAM_STRING		= "seqorder%2Cseqorder_time%2Cid%2Cname%2Cdescription%2Ctype_en%2Ctype_cn%2Cbrand_en%2Cbrand_cn%2Cpurchasing_price%2Clocal_price%2Cpost_time%2Cimg_urls%2Cimg_urls_m%2Csn";

	/**
	 * 刷新的条数
	 */
	public static final int		INIT_NUM			= 10;
	/**
	 * 更多的条数
	 */
	public static final int		MORE_NUM			= 10;

	public static final String	MESSAGE_NODATA		= "暂无更新";
	public static final String	MESSAGE_LAST		= "已经是最后一条";
	public static final String	MESSAGE_EXCEPTION	= "获取数据异常";
	public static final String	MESSAGE_NET			= "获取数据失败，请检查网络";

}
