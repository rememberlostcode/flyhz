
package com.holding.smile.tools;

import android.content.Context;
import android.content.Intent;
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
	
	public static void sendEmail(Context context) {
		Intent email = new Intent(android.content.Intent.ACTION_SEND);
		email.setType("plain/text");
		String[] emailReciver = new String[]{"service@tiantianhaigou.com"};
		String emailSubject = "请输入标题";
		String emailBody = "请输入内容（疑问、建议、要求等等）";

		//设置邮件默认地址
		email.putExtra(android.content.Intent.EXTRA_EMAIL, emailReciver);
		//设置邮件默认标题
		email.putExtra(android.content.Intent.EXTRA_SUBJECT, emailSubject);
		//设置要默认发送的内容
		email.putExtra(android.content.Intent.EXTRA_TEXT, emailBody);
		//调用系统的邮件系统
		context.startActivity(Intent.createChooser(email, "请选择邮件发送软件"));
	}
}
