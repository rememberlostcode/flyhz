
package com.flyhz.framework.util;

public class Constants {
	/***************************** solr start *************************/
	/**
	 * solr搜索链接
	 */
	public static final String	SEARCH_URL				= "/solr/smile/select";
	/**
	 * 静态图片路径前缀，拼接时前面加上服务器地址，后门加上数据库中的字段，
	 * 结果如：http://10.22.22.40/smile/static/cacoh/images/tmp.jpg
	 */
	public static final String	IMAGES_URL				= "/static";

	/**
	 * solr搜索结果的需要返回的物品属性
	 */
	public static final String	PARAM_STRING			= "seqorder%2Cseqorder_time%2Cid%2Cname%2Cdescription%2Ctype_en%2Ctype_cn%2Cbrand_en%2Cbrand_cn%2Cpurchasing_price%2Clocal_price%2Cpost_time%2Cimg_urls%2Cimg_urls_m%2Csn";

	/**
	 * 刷新的条数
	 */
	public static final int		INIT_NUM				= 10;
	/**
	 * 更多的条数
	 */
	public static final int		MORE_NUM				= 10;

	public static final String	MESSAGE_NODATA			= "暂无更新";
	public static final String	MESSAGE_LAST			= "已经是最后一条";
	public static final String	MESSAGE_EXCEPTION		= "获取数据异常";
	public static final String	MESSAGE_NET				= "获取数据失败，请检查网络";
	/***************************** solr end *************************/

	/***************************** redis start *************************/
	/**
	 * 分类前缀
	 */
	public static final String	PREFIX_CATS				= "smile@cates";
	/**
	 * 品牌前缀
	 */
	public static final String	PREFIX_BRANDS			= "smile@brands";
	/**
	 * 品牌推荐商品前缀
	 */
	public static final String	PREFIX_BRANDS_RECOMMEND	= "smile@brands@recommend";
	/**
	 * 各品牌各分类推荐商品前缀
	 */
	public static final String	PREFIX_BRANDS_CATS		= "smile@brands&cates";
	/***************************** redis end *************************/
}
