
package com.holding.smile.tools;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * 提示工具类 2014-06-23
 * 
 * @author zhangb
 * 
 */
public class ToastUtils {
	private ToastUtils() {
	}

	/**
	 * 短暂地在屏幕中间显示提示
	 * 
	 * @param context
	 * @param message
	 */
	public static void showShort(Context context, String message) {
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	/**
	 * 在屏幕中间显示提示
	 * 
	 * @param context
	 * @param message
	 */
	public static void showLong(Context context, String message) {
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	public static void show(Context context, String message, int duration) {
		Toast.makeText(context, message, duration).show();
	}
}
