
package com.holding.smile.tools;

public class Constants {

	public static final String	MESSAGE_NODATA		= "暂无更新";
	public static final String	MESSAGE_LAST		= "已经是最后一条";
	public static final String	MESSAGE_EXCEPTION	= "获取数据异常";
	public static final String	MESSAGE_NET			= "获取数据失败，请检查网络";
	public static final String	TBHG_PREFIX			= "海狗订单号(不可修改)：";	// 淘宝卖家留言前缀

	/**
	 * 本地数据库国家表中的中国ID
	 */
	public static final int		CONTURY_ID			= 1;

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
}
