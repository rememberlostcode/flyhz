
package com.holding.smile.tools;

import android.R;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

public class ClickUtil {

	// /**
	// * 状态按钮单击事件及按钮名称设置
	// *
	// * @param statusButton
	// * @param status
	// */
	// public static void setClickForStatus(final Context context, Button
	// statusButton, String status,
	// final String number) {
	// // 10待支付；11支付中；12已支付；13缺少身份证；14已有身份证；15发货中；
	// // 20已发货；21国外清关；30国内清关；40国内物流；50已关闭；60已完成；70已删除；
	// int statusInt =
	// Integer.parseInt(Constants.OrderStateCode.FOR_PAYMENT.code);
	// if (status != null && !"".equals(status.trim())) {
	// statusInt = Integer.parseInt(status);
	// }
	// statusButton.setText(getTextByStatus(status));
	//
	// switch (statusInt) {
	// case 10:
	// statusButton.setOnClickListener(new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// // Toast.makeText(context, "去付款",
	// // Toast.LENGTH_SHORT).show();
	// Intent intent = new Intent(context, WebViewActivity.class);
	// intent.putExtra("number", number);
	// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	// context.startActivity(intent);
	// }
	// });
	// break;
	// case 11:
	// break;
	// case 12:
	// break;
	// case 13:
	// statusButton.setOnClickListener(new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// Intent intent = new Intent();
	// intent.setClass(context, IdcardManagerActivity.class);
	// intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
	// context.startActivity(intent);
	// }
	// });
	// break;
	// case 14:
	// break;
	// case 15:
	// break;
	// case 20:
	// break;
	// case 21:
	// break;
	// case 30:
	// break;
	// case 40:
	// break;
	// case 50:
	// break;
	// case 60:
	// break;
	// case 70:
	// break;
	// default:
	// break;
	// }
	// }

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
