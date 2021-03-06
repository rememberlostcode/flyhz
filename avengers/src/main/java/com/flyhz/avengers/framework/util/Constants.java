
package com.flyhz.avengers.framework.util;

public class Constants {
	/***************************** solr first *************************/
	/**
	 * 静态图片路径前缀，拼接时前面加上服务器地址，后门加上数据库中的字段，
	 * 结果如：http://10.22.22.40/smile/static/cacoh/images/tmp.jpg
	 */
	public static final String	IMAGES_URL						= "/static";
	/**
	 * solr搜索结果的需要返回的物品属性
	 */
	public static final String	PARAM_STRING					= "id%2Cname";
	/**
	 * 刷新的条数
	 */
	public static final int		INIT_NUM						= 10;
	/**
	 * 更多的条数
	 */
	public static final int		MORE_NUM						= 10;
	/***************************** solr second *************************/

	/***************************** redis first *************************/
	/**
	 * 各品牌各分类推荐商品KEY前缀(选分类时)，redis中完整的key=smile@brands@recommend&cates@%cid%
	 */
	public static final String	PREFIX_BRANDS_RECOMMEND_CATES	= "smile@brands@recommend&cates@";
	/**
	 * 用户所有的订单KEY前缀，使用时再加上用户ID，如smile@orders@user@%userId%
	 */
	public static final String	PREFIX_ORDERS_USER				= "smile@orders@user@";
	/**
	 * 品牌推荐商品KEY（即不选分类时）
	 */
	public static final String	REDIS_KEY_BRANDS_RECOMMEND		= "smile@brands@recommend";
	/**
	 * 首页活动推荐KEY，类型为String
	 */
	public static final String	REDIS_KEY_RECOMMEND_INDEX		= "smile@recommend@index";
	/**
	 * 所有分类KEY，类型为map<catelogyId,catelogyJson>
	 */
	public static final String	REDIS_KEY_CATES					= "smile@cates@all";
	/**
	 * 所有品牌KEY，类型为map<brandId,brandJson>
	 */
	public static final String	REDIS_KEY_BRANDS				= "smile@brands@all";
	/***************************** redis second *************************/

	public static final String	MESSAGE_NODATA					= "暂无更新";
	public static final String	MESSAGE_LAST					= "已经是最后一条";
	public static final String	MESSAGE_EXCEPTION				= "获取数据异常";
	public static final String	MESSAGE_NET						= "获取数据失败，请检查网络";

	public static enum OrderStateCode {

		FOR_PAYMENT("10"), // 10待支付；
		HAVE_BEEN_PAID("12"), // 12已支付；
		THE_LACK_OF_IDENTITY_CARD("13"), // 13缺少身份证；
		HAS_ID("14"), // 14已有身份证；
		WAITING_FOR_DELIVERY("15"), // 15等待发货；
		SHIPPED_ABROAD_CLEARANCE("20"), // 20已发货；
		FOREIGN_CUSTOMS_CLEARANCE("21"), // 21国外清关；
		DOMESTIC_CUSTOMS_CLEARANCE("30"), // 30国内清关；
		DOMESTIC_LOGISTICS("40"), // 40国内物流；
		HAVE_BEEN_CLOSED("50"), // 50已关闭；
		HAS_BEEN_COMPLETED("60"), // 60已完成；
		DELETED("70");// 70已删除；

		public String	code;

		private OrderStateCode(String code) {
			this.code = code;
		}
	}

	// 定义文件保存目录前缀
	public static final String	IMAGE_PREFIX_PATH	= "D:\\oklfile";
}
