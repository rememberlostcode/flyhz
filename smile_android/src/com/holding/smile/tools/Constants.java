
package com.holding.smile.tools;

public class Constants {

	public static final String	MESSAGE_NODATA		= "暂无更新";
	public static final String	MESSAGE_LAST		= "已经是最后一条";
	public static final String	MESSAGE_EXCEPTION	= "获取数据异常";
	public static final String	MESSAGE_NET			= "获取数据失败，请检查网络";

	/**
	 * 本地数据库国家表中的中国ID
	 */
	public static final int		CONTURY_ID			= 1;

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
}
