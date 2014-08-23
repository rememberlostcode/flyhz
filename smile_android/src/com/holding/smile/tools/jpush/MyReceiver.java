
package com.holding.smile.tools.jpush;

import org.json.JSONException;
import org.json.JSONObject;

import com.holding.smile.activity.HtmlUIActivity;
import com.holding.smile.activity.IdcardManagerActivity;
import com.holding.smile.activity.LoginActivity;
import com.holding.smile.activity.MyApplication;
import com.holding.smile.activity.MyOrdersActivity;
import com.holding.smile.entity.SUser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String	TAG	= "JPush";

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: "
				+ printBundle(bundle));

		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
			// send the Registration Id to your server...

		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
			Log.d(TAG,
					"[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
			processCustomMessage(context, bundle);

		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
			Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
			int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
			Log.d(TAG, "[MyReceiver] 用户点击打开了通知");

			JPushInterface.reportNotificationOpened(context,
					bundle.getString(JPushInterface.EXTRA_MSG_ID));

			String alert = bundle.getString(JPushInterface.EXTRA_ALERT);
			// 根据标题打开自定义的Activity
			if (alert.indexOf("已发货") > -1) {
				if (!MyApplication.getInstance().getLoginService().isSessionInvalidated()) {
					Intent i = new Intent();
					i.putExtra("class", MyOrdersActivity.class);
					i.setClass(context, LoginActivity.class);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(i);
				} else {
					Intent i = new Intent();
					i.setClass(context, MyOrdersActivity.class);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(i);
				}
			} else if (alert.indexOf("活动") > -1) {
				String urlJson = bundle.getString(JPushInterface.EXTRA_EXTRA);
				JSONObject job;
				String url = null;
				try {
					job = new JSONObject(urlJson);
					if (job.getString("url") != null)
						url = job.getString("url").toString();
				} catch (JSONException e) {
					Log.e(TAG, e.getMessage());
				}
				Intent i = new Intent(context, HtmlUIActivity.class);
				i.putExtra("url", url);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
			} else if (alert.indexOf("身份证") > -1) {
				SUser user = MyApplication.getInstance().getCurrentUser();
				Intent i = null;
				if (user == null || MyApplication.getInstance().getSessionId() == null) {
					i = new Intent(context, LoginActivity.class);
					i.putExtra("class", IdcardManagerActivity.class);
				} else {
					i = new Intent(context, IdcardManagerActivity.class);
				}
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
			} else {
				Log.i(TAG, "未进行任何操作");
			}
		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
			Log.d(TAG,
					"[MyReceiver] 用户收到到RICH PUSH CALLBACK: "
							+ bundle.getString(JPushInterface.EXTRA_EXTRA));
			// 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
			// 打开一个网页等..

		} else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
			boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE,
					false);
			Log.e(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to "
					+ connected);
		} else {
			Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
		}
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}

	// send msg to MainActivity
	private void processCustomMessage(Context context, Bundle bundle) {
		String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		/*if (message.indexOf("身份证") > -1) {
			Log.e(MyApplication.LOG_TAG, "设置身份证缺少标记为1");
			if (MyApplication.getInstance().getLoginService().isSessionInvalidated()) {
				MyApplication.getInstance().getCurrentUser().setIsmissidcard("1");
				MyApplication.getInstance().getSqliteService().updateUser();
			}
		} else */
		if (message.indexOf("购物车") > -1) {
			Log.e(MyApplication.LOG_TAG, "添加购物车数量");
			try {
				JSONObject extraJson = new JSONObject(message);
				Integer addNum = extraJson.getInt("qty");
				if (addNum != null
						&& MyApplication.getInstance().getLoginService().isSessionInvalidated()) {
					MyApplication.getInstance().getSqliteService().addUserShoppingCount(addNum);
				} else {
					Log.w(MyApplication.LOG_TAG, "未登录，跳过添加购物车数量");
				}
			} catch (JSONException e) {
				Log.d(MyApplication.LOG_TAG, e.getMessage());
			}
		}
//		if (IndexActivity.isForeground) {
//			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//			Intent msgIntent = new Intent(IndexActivity.MESSAGE_RECEIVED_ACTION);
//			msgIntent.putExtra(IndexActivity.KEY_MESSAGE, message);
//			if (!ExampleUtil.isEmpty(extras)) {
//				try {
//					JSONObject extraJson = new JSONObject(extras);
//					if (null != extraJson && extraJson.length() > 0) {
//						msgIntent.putExtra(IndexActivity.KEY_EXTRAS, extras);
//					}
//				} catch (JSONException e) {
//
//				}
//
//			}
//			context.sendBroadcast(msgIntent);
//		}
	}
}
