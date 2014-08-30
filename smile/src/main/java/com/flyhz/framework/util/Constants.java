
package com.flyhz.framework.util;

import java.math.BigDecimal;

public class Constants {
	/***************************** solr start *************************/
	/**
	 * 刷新的条数
	 */
	public static final int		INIT_NUM						= 10;
	/**
	 * 更多的条数
	 */
	public static final int		MORE_NUM						= 10;
	/***************************** solr end *************************/

	/***************************** redis start *************************/
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
	/**
	 * APK版本KEY
	 */
	public static final String	REDIS_KEY_VERSION				= "smile@version";
	/***************************** redis end *************************/

	public static final String	MESSAGE_NODATA					= "暂无更新";
	public static final String	MESSAGE_LAST					= "已经是最后一条";
	public static final String	MESSAGE_EXCEPTION				= "获取数据异常";
	public static final String	MESSAGE_NET						= "获取数据失败，请检查网络";
	
	public static final String	TBHG_PREFIX			= "海狗订单号(不可修改)：";	// 淘宝卖家留言前缀

	public static enum OrderStateCode {

		FOR_PAYMENT("10"), // 10待支付；
		HAVE_BEEN_PAID("12"), // 12已支付；
		AMOUNT_ERROR("13"), // 13金额错误
		THE_LACK_OF_IDENTITY_CARD("14"), // 14缺少身份证；
		HAS_ID("15"), // 15已有身份证；
		WAITING_FOR_DELIVERY("16"), // 16等待发货；
		SHIPPED_ABROAD_CLEARANCE("20"), // 20已发货；
		FOREIGN_CUSTOMS_CLEARANCE("21"), // 21国外清关；
		DOMESTIC_CUSTOMS_CLEARANCE("30"), // 30国内清关；
		DOMESTIC_LOGISTICS("40"), // 40国内物流；
		DELIVERY("41"), // 41已收货
		HAVE_BEEN_CLOSED("50"), // 50已关闭；
		HAS_BEEN_COMPLETED("60"), // 60已完成；
		DELETED("70");// 70已删除；

		public String	code;

		private OrderStateCode(String code) {
			this.code = code;
		}
	}

	public static final String	TB_URL	= "http://211.149.175.138/smile/build/url";

	public static Double		dollarExchangeRate;
	
	
	/**
	 * 淘宝配置文件全路径，taobao.properties
	 */
	public static String		propertiesFilePath;
	
	
	public static final BigDecimal logisticsPriceEvery = new BigDecimal(150);
}
