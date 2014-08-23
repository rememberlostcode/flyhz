
package com.flyhz.framework.lang;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.framework.util.JSONUtil;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.APIConnectionException;
import cn.jpush.api.common.APIRequestException;

/**
 * 极光推送
 * 
 * @author zhangb
 * 
 */
public class JPush {
	private Logger			log				= LoggerFactory.getLogger(JPush.class);
	private String			appKey			= "781366198c9fc60a92f286bc";
	private String			masterSecret	= "38992c75ced1bf11289ca48e";
	private JPushClient		jpush;
	private int				maxRetryTimes	= 86400;
	private final String	title			= "天天海狗";

	public JPush() {
		jpush = new JPushClient(masterSecret, appKey, maxRetryTimes);
	}

	public JPush(String appKey, String masterSecret) {
		this.appKey = appKey;
		this.masterSecret = masterSecret;
		jpush = new JPushClient(masterSecret, appKey, maxRetryTimes);
	}

	public JPush(String appKey, String masterSecret, int maxRetryTimes) {
		this.appKey = appKey;
		this.masterSecret = masterSecret;
		this.maxRetryTimes = maxRetryTimes;
		jpush = new JPushClient(masterSecret, appKey, maxRetryTimes);
	}

	public static void main(String[] args) {
		JPush jPush = new JPush();
		String registrationID = "0508a806f24";
		
//		String alert = "测试alert";
//		String msgContent = "测试msgContent";

		// jPush.sendMessageAll(msgContent);
//		Map<String, String> map = new HashMap<String, String>();
//		map.put("qty", "1");
//		jPush.sendAndroidMessageWithRegistrationID("购物车", map, registrationID);

		// Map<String, String> extras = new HashMap<String, String>();
		// extras.put("id", "1");
		// extras.put("name", "zhangbin");
		// jPush.sendAndroidNotificationWithRegistrationID(alert, extras,
		// registrationID);

		// Map<String, String> map = new HashMap<String, String>();
		// map.put("qty","1");
		// jPush.sendAndroidNotificationWithRegistrationID("您的购物车有新的商品！",
		// map, registrationID);
		
		Map<String, String> extras = new HashMap<String, String>();
		extras.put("number", "2222222");
		jPush.sendAndroidNotificationWithRegistrationID("由于海关需要，您的订单收件人缺少必要身份证，您需要上传！", extras,
				registrationID);
	}

	/**
	 * 发送到所有用户通知
	 * 
	 * @param msgContent
	 */
	public void sendMessageAll(String msgContent) {
		try {
			jpush.sendNotificationAll(msgContent);
		} catch (APIConnectionException e) {
			log.error(e.getMessage());
		} catch (APIRequestException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * 发送android自定义消息
	 * 
	 * @param alert
	 * @param extras
	 * @param registrationID
	 */
	public void sendAndroidMessageWithRegistrationID(String alert, Map<String, String> extras,
			String registrationID) {
		try {
			extras.put("message", alert);
			jpush.sendAndroidMessageWithRegistrationID(title, JSONUtil.getEntity2Json(extras),
					registrationID);
		} catch (APIConnectionException e) {
			log.error(e.getMessage());
		} catch (APIRequestException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * 发送android通知
	 * 
	 * @param alert
	 * @param extras
	 * @param registrationID
	 */
	public void sendAndroidNotificationWithRegistrationID(String alert, Map<String, String> extras,
			String registrationID) {
		try {
			jpush.sendAndroidNotificationWithRegistrationID(title, alert, extras, registrationID);
		} catch (APIConnectionException e) {
			log.error(e.getMessage());
		} catch (APIRequestException e) {
			log.error(e.getMessage());
		}
	}

}
