
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
	public static final String	PARAM_STRING			= "id%2Cname";
	/**
	 * 刷新的条数
	 */
	public static final int		INIT_NUM				= 10;
	/**
	 * 更多的条数
	 */
	public static final int		MORE_NUM				= 10;
	/***************************** solr end *************************/

	/***************************** redis start *************************/
	/**
	 * 指定用户的订单key前缀，redis中完整的key=smile@user@orders@%userId%
	 */
	public static final String	PREFIX_USER_ORDERS		= "smile@user@orders";
	/**
	 * 分类key前缀，redis中完整的key=smile@cates@all
	 */
	public static final String	PREFIX_CATES			= "smile@cates";
	/**
	 * 品牌key前缀，redis中完整的key=smile@brands@all
	 */
	public static final String	PREFIX_BRANDS			= "smile@brands";
	/**
	 * 推荐商品key前缀（首页最顶端的商品），redis中完整的key=smile@recommend@index
	 */
	public static final String	PREFIX_RECOMMEND		= "smile@recommend";
	/**
	 * 品牌推荐商品key前缀（即不选分类时），redis中完整的key=smile@brands@recommend@%bid%
	 */
	public static final String	PREFIX_BRANDS_RECOMMEND	= "smile@brands@recommend";
	/**
	 * 各品牌各分类推荐商品key前缀，redis中完整的key=smile@brands&cates@%cid%_%bid%
	 */
	public static final String	PREFIX_BRANDS_CATES		= "smile@brands&cates";
	/***************************** redis end *************************/

	public static final String	MESSAGE_NODATA			= "暂无更新";
	public static final String	MESSAGE_LAST			= "已经是最后一条";
	public static final String	MESSAGE_EXCEPTION		= "获取数据异常";
	public static final String	MESSAGE_NET				= "获取数据失败，请检查网络";
}
