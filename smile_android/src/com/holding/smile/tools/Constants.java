
package com.holding.smile.tools;

public class Constants {

	/**
	 * solr服务器地址
	 */
	public static String		SERVER_URL			= "http://10.22.22.40";
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

	/**
	 * 图片URL前缀,用于和数据库的相对路径拼接组成完整路径
	 * 
	 * @return
	 */
	public static String getImageUrlPre() {
		return SERVER_URL + IMAGES_URL;
	}
}
