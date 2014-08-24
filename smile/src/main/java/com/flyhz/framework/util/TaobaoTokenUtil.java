
package com.flyhz.framework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taobao.api.internal.util.WebUtils;

/**
 * 淘宝session key获取工具
 * @author zhangb
 *
 */
public class TaobaoTokenUtil {
	private static Logger					log							= LoggerFactory.getLogger(TaobaoTokenUtil.class);

	private static String					taobaoPropertiesFilePath	= "C:/Users/silvermoon/taobao.properties";
	private static String					appKey;
	private static String					appSecret;
	private static String					accessToken;
	private static String					sellerNick;

	private static String					tbPostSessionUrl;
	private static String					redirectUri;
	private static String					refreshToken;
	private static String					expiresIn;
	private static String					lastModifyTime;
	private static final String				state						= "1212";
	private static final int				connectTimeout				= 3000;
	private static final int				readTimeout					= 3000;

	public static HashMap<String, String>	config						= new HashMap<String, String>();
	static {
		/* 定时任务，每隔一段时间获取授权码 */
		Timer timer = new Timer();
		long delay = 10 * 60 * 1000;// 在豪秒后执行此任务
		long period = 60 * 60 * 1000;// 每次间隔豪秒/每个小时执行一次
		timer.schedule(new MyTask(), delay, period);
	}
	
	public static void init(){
		log.info("TaobaoTokenUtil init ...");
		taobaoPropertiesFilePath = Constants.propertiesFilePath;

		initPropertiesFromFile();
	}
	/**
	 * 从文件读取数据初始化参数
	 */
	private static void initPropertiesFromFile(){
		StringBuffer sb = new StringBuffer();
		Properties props = new Properties();
		if (StringUtils.isNotBlank(taobaoPropertiesFilePath)) {
			InputStream is = null;
			try {
				File file = new File(taobaoPropertiesFilePath);
				if (file.exists()) {
					is = new FileInputStream(file);
					props.load(is);
				} else {
					log.info("淘宝配置文件不存在，请添加taobao.properties");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				Set<Object> set = props.keySet();
				Iterator<Object> it = set.iterator();
				while (it.hasNext()) {
					String key = it.next().toString();
					String value = props.get(key) + "";
					config.put(key, value);
					sb.append("" + key + "=" + value + "\n");
				}
				log.info(taobaoPropertiesFilePath + ":\n{\n" + sb.toString() + "}");
			} catch (Exception e) {
				e.printStackTrace();
			}
			appKey = config.get("appKey");
			appSecret = config.get("appSecret");
			tbPostSessionUrl = config.get("tbPostSessionUrl");
			redirectUri = config.get("redirectUri");
			accessToken = config.get("accessToken");
			refreshToken = config.get("refreshToken");
			expiresIn = config.get("expiresIn");
			lastModifyTime = config.get("lastModifyTime");
			sellerNick = config.get("sellerNick");
		} else {
			log.info("taobaoPropertiesFilePath is null!!!");
		}
	}

	static class MyTask extends java.util.TimerTask {
		@Override
		public void run() {
			initPropertiesFromFile();
			getTaobaoAccessToken();
		}
	}

	public static void main(String[] args) {
		log.info(getTaobaoAccessToken());
	}

	public static String getTaobaoAccessToken() {
		if (lastModifyTime != null && !"".equals(lastModifyTime.trim())) {
			// 如果在失效时间内，则直接返回之前的accessToken
			try {
				long lastTime = Long.parseLong(lastModifyTime);
				long nowTime = (new Date().getTime()) / 1000;
				long intervalTime = nowTime - lastTime;
				long expiresTime = Long.parseLong(expiresIn);
				if (expiresTime > intervalTime) {
					log.info("accessToken还没有失效，不需要刷新token!");
					return accessToken;
				} else {
					log.info("token有效期还有" + intervalTime /60 + "分钟");
				}
			} catch (NumberFormatException e) {
				log.error(e.getMessage());
			}
		}
		if (refreshToken == null || "".equals(refreshToken.trim())) {
			// 如果refreshToken为空，则通过初始授权码获取refreshToken
			if (accessToken != null) {
				log.info("跳过，暂定不用此方式获取token!");
//				getRefreshTokenByAccessToken();
			} else {
				log.info("初始授权码和刷新授权码都为空，无法刷新!");
			}

			if (refreshToken == null || "".equals(refreshToken.trim())) {
				log.info("通过初始授权码获取refreshToken失败!");
				return null;
			}
		}
		// 通过refreshToken获取授权码accessToken(即session)和refreshToken(new)
		log.info("accessToken已失效，将要刷新!");
		log.info("跳过，暂定不用此方式获取token!");
//		getAccessTokenAndRefreshTokenByRefreshToken();
		if (refreshToken != null && !"".equals(refreshToken.trim())) {
			boolean writeResult = writeToken();
			if (!writeResult) {
				log.info("将accessToken和refreshToken写入taobao.properties失败!");
				return null;
			}
		}
		return accessToken;
	}
	
	public static boolean writeToken(){
		// 将accessToken和refreshToken写入taobao.properties
		lastModifyTime = (new Date().getTime()) / 1000 + "";
		config.put("accessToken", accessToken);
		config.put("refreshToken", refreshToken);
		config.put("expiresIn", expiresIn);
		config.put("lastModifyTime",  lastModifyTime);
		boolean writeResult = writeFile(config);
		return writeResult;
	}

	/**
	 * 通过初始授权码获取refreshToken
	 */
	private static void getRefreshTokenByAccessToken() {
		try {
			Map<String, String> param = new HashMap<String, String>();
			param.put("grant_type", "authorization_code");
			param.put("code", accessToken);
			param.put("client_id", appKey);
			param.put("client_secret", appSecret);
			param.put("redirect_uri", redirectUri);
			param.put("view", "web");
			param.put("state", state);
			String responseJson = WebUtils.doPost(tbPostSessionUrl, param, connectTimeout,
					readTimeout);
			log.info(responseJson);
			JSONObject jsonObj = new JSONObject(responseJson);
			refreshToken = jsonObj.getString("refresh_token");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通过refreshToken获取授权码accessToken(即session)和refreshToken(new)
	 * 
	 * @return 授权码accessToken(即session)
	 */

	private static void getAccessTokenAndRefreshTokenByRefreshToken() {
		try {
			Map<String, String> param = new HashMap<String, String>();
			param.put("grant_type", "refresh_token");
			param.put("refresh_token", refreshToken);
			param.put("client_id", appKey);
			param.put("client_secret", appSecret);
			param.put("view", "web");
			param.put("state", state);
			String responseJson = WebUtils.doPost(tbPostSessionUrl, param, connectTimeout,
					readTimeout);
			log.info(responseJson);
			JSONObject jsonObj = new JSONObject(responseJson);
			accessToken = jsonObj.getString("access_token");
			refreshToken = jsonObj.getString("refresh_token");
//			expiresIn = jsonObj.getString("expires_in");
			log.info("获取到新的token：");
			log.info("accessToken=" + accessToken);
			log.info("refreshToken=" + refreshToken);
			log.info("expiresIn=" + expiresIn);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 把map中的淘宝信息写入文件
	 * @param map
	 * @return
	 */
	public static boolean writeFile(HashMap<String, String> map) {
		boolean result = false;
		FileWriter fw = null;
		try {
			File file = new File(taobaoPropertiesFilePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			fw = new FileWriter(file.getAbsolutePath());
			Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
				String key = entry.getKey();
				String value = map.get(key);
				fw.write(key + "=" + value + "\n");
			}
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			try {
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static String getAppKey() {
		return appKey;
	}

	public static void setAppKey(String appKey) {
		TaobaoTokenUtil.appKey = appKey;
	}

	public static String getAppSecret() {
		return appSecret;
	}

	public static void setAppSecret(String appSecret) {
		TaobaoTokenUtil.appSecret = appSecret;
	}

	public static String getSellerNick() {
		return sellerNick;
	}

	public static void setSellerNick(String sellerNick) {
		TaobaoTokenUtil.sellerNick = sellerNick;
	}

	public static void setAccessToken(String accessToken) {
		TaobaoTokenUtil.accessToken = accessToken;
	}

	public static String getAccessToken() {
		return accessToken;
	}
}
