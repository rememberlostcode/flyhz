
package com.flyhz.framework.lang;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		String registrationID = "02097b1d5f5";
		String alert = "测试alert";
		String msgContent = "测试msgContent";

		jPush.sendMessageAll(msgContent);

		jPush.sendIosMessageWithRegistrationID(msgContent, registrationID);

		Map<String, String> extras = new HashMap<String, String>();
		extras.put("id", "1");
		extras.put("name", "zhangbin");
		jPush.sendAndroidNotificationWithRegistrationID(alert, extras, registrationID);
	}

	/**
	 * 发送到所有用户
	 * 
	 * @param msgContent
	 */
	public void sendMessageAll(String msgContent) {
		try {
			jpush.sendMessageAll(msgContent);
		} catch (APIConnectionException e) {
			log.error(e.getMessage());
		} catch (APIRequestException e) {
			log.error(e.getMessage());
		}
	}

	public void sendIosMessageWithRegistrationID(String msgContent, String registrationID) {
		try {
			jpush.sendAndroidMessageWithRegistrationID(title, msgContent, registrationID);
		} catch (APIConnectionException e) {
			log.error(e.getMessage());
		} catch (APIRequestException e) {
			log.error(e.getMessage());
		}
	}

	public void sendAndroidNotificationWithRegistrationID(String alert, Map<String, String> extras,
			String registrationID) {
		try {
			jpush.sendAndroidNotificationWithRegistrationID("天天海狗", alert, extras, registrationID);
		} catch (APIConnectionException e) {
			log.error(e.getMessage());
		} catch (APIRequestException e) {
			log.error(e.getMessage());
		}
	}

}
