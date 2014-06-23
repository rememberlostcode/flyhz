
package com.holding.smile.tools;

import android.graphics.Color;

public class ClickUtil {


	public static String getTextByStatus(String status) {
		String text = "去支付";
		if (status == null) {
			return text;
		}
		int ints = Integer.parseInt(status);
		switch (ints) {
			case 10:
				text = "去支付";
				break;
			case 11:
				text = "支付中";
				break;
			case 12:
				text = "已支付";
				break;
			case 13:
				text = "上传身份证";
				break;
			case 14:
				text = "已有身份证";
				break;
			case 15:
				text = "发货中";
				break;
			case 20:
				text = "已发货";
				break;
			case 21:
				text = "国外清关";
				break;
			case 30:
				text = "国内清关";
				break;
			case 40:
				text = "国内物流";
				break;
			case 50:
				text = "已关闭";
				break;
			case 60:
				text = "已完成";
				break;
			case 70:
				text = "已删除";
				break;
			default:
				break;
		}
		return text;
	}
	
	public static int getBackgroundColorByStatus(String status) {
		int colorId = Color.parseColor("#cfcfcf");
		if (status == null) {
			return colorId;
		}
		int ints = Integer.parseInt(status);
		switch (ints) {
			case 10:
				//"去支付";
				colorId = Color.parseColor("#46cf99");
				break;
			case 11:
				//"支付中";
				break;
			case 12:
				//已支付";
				break;
			case 13:
				//上传身份证";
				break;
			case 14:
				//已有身份证";
				break;
			case 15:
				//发货中";
				break;
			case 20:
				//已发货";
				break;
			case 21:
				//国外清关";
				break;
			case 30:
				//国内清关";
				break;
			case 40:
				//国内物流";
				break;
			case 50:
				//已关闭";
				break;
			case 60:
				//已完成";
				break;
			case 70:
				//已删除";
				break;
			default:
				break;
		}
		return colorId;
	}
}
