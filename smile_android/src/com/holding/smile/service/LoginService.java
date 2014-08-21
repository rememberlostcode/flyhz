
package com.holding.smile.service;

import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import android.content.Context;
import android.util.Log;

import com.holding.smile.R;
import com.holding.smile.activity.MyApplication;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.entity.SUser;
import com.holding.smile.protocol.PUser;
import com.holding.smile.tools.CodeValidator;
import com.holding.smile.tools.JSONUtil;
import com.holding.smile.tools.NullHostNameVerifier;
import com.holding.smile.tools.NullX509TrustManager;
import com.holding.smile.tools.URLUtil;

public class LoginService {

	private Context		context;
	private String		login_url;
	private String		auto_login_url;

	/**
	 * 成功的code
	 */
	private final int	SUCCESS_CODE	= 200000;
	/**
	 * 手机端超过多少时间后需要重新验证是否登录失效的毫秒值
	 */
	private final int	SESSION_TIME	= 25 * 1000 * 60;

	public LoginService(Context context) {
		super();
		this.context = context;

		login_url = context.getString(R.string.login_url);
		auto_login_url = context.getString(R.string.auto_login_url);
	}

	/**
	 * 判断session是否已经失效，如果失效重新登录;如重新登录失败，清除当前登录人信息
	 */
	public boolean isSessionInvalidated() {
		boolean isLogining = true;
		if (MyApplication.getSessionTime() == null) {//
			SUser user = MyApplication.getInstance().getSqliteService().getScurrentUser();
			if (user != null) {
				RtnValueDto rtnValueDto = MyApplication.getInstance().getLoginService()
														.autoLogin();
				if (rtnValueDto == null || !rtnValueDto.getCode().equals(SUCCESS_CODE)) {
					MyApplication.getInstance().setCurrentUser(null);
					MyApplication.getInstance().setSessionId(null);
					isLogining = false;
				}
			}
		} else {
			if (new Date().getTime() - MyApplication.getSessionTime().getTime() > SESSION_TIME) {// 超过20分钟需确认下是否登录失效
				System.out.println(MyApplication.getSessionTime());
				Log.i(MyApplication.LOG_TAG, "超过20分钟，需要重新登录");
				SUser user = MyApplication.getInstance().getCurrentUser();
				if (user != null) {
					RtnValueDto rtnValueDto = MyApplication.getInstance().getLoginService()
															.autoLogin();
					if (rtnValueDto == null || !rtnValueDto.getCode().equals(SUCCESS_CODE)) {
						MyApplication.getInstance().setCurrentUser(null);
						MyApplication.getInstance().setSessionId(null);
						isLogining = false;
						Log.i(MyApplication.LOG_TAG, "重新登录失败");
					} else {
						Log.i(MyApplication.LOG_TAG, "重新登录成功");
					}
				}
			} else {
				
			}
		}
		return isLogining;
	}

	/**
	 * 用户名密码登录
	 * 
	 * @param iuser
	 * @return
	 */
	public RtnValueDto login(SUser iuser,boolean isAutoLogin) {
		if(CodeValidator.isNetworkError()){
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto rvd = null;
		HashMap<String, String> param = new HashMap<String, String>();
		param.put("username", iuser.getUsername());
		param.put("password", iuser.getPassword());
		if (iuser.getRegistrationID() != null) {
			param.put("registrationID", iuser.getRegistrationID());
		}
		String rvdString = null;
		if (login_url.indexOf("https") > -1) {
			rvdString = https_get(login_url, param);
		} else {
			rvdString = URLUtil.getStringByGet(login_url, param);
		}
		if (rvdString != null) {
			PUser user = JSONUtil.getJson2Entity(rvdString, PUser.class);
			if (user != null) {
				MyApplication.setSessionTime(new Date());
				rvd = new RtnValueDto();
				rvd.setUserData(user.getData());
				rvd.setCode(user.getCode());
				if (user.getData() != null && user.getCode() == SUCCESS_CODE) {
					try {
						SUser suser = MyApplication.getInstance().getSqliteService().getScurrentUser();
						if (suser == null) {
							suser = new SUser();
						}
						if (iuser.getRegistrationID() != null) {
							suser.setRegistrationID(iuser.getRegistrationID());
						}
						if(user.getData().getEmail()!=null){
							suser.setEmail(user.getData().getEmail());
						}
						if(user.getData().getMobilephone()!=null){
							suser.setMobilephone(user.getData().getMobilephone());
						}
						if(user.getData().getToken()!=null){
							suser.setToken(user.getData().getToken());
						}
						setLoginUser(suser);
						if (isAutoLogin) {
							// 添加本地数据库用户信息
							MyApplication.getInstance().getSqliteService().addUser();
						}
					} catch (Exception e) {
						System.out.println("SQLite出错了！");
						e.printStackTrace();
						System.out.println("SQLite关闭并初始化！");
						MyApplication.getInstance().getSqliteService().closeDB();
						MyApplication.getInstance().setSqliteService(new SQLiteService(context));
					}
				}
			}
		}
		return rvd;
	}

	public RtnValueDto autoLogin() {
		if(CodeValidator.isNetworkError()){
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		SUser iuser = MyApplication.getInstance().getCurrentUser();
		
		if (MyApplication.getInstance().getRegistrationID() != null) {
			iuser.setRegistrationID(MyApplication.getInstance().getRegistrationID());
		}
		RtnValueDto rvd = null;
		if (iuser != null && iuser.getUsername() != null && iuser.getToken() != null) {
			HashMap<String, String> param = new HashMap<String, String>();
			param.put("username", iuser.getUsername());
			param.put("token", iuser.getToken());
			if (iuser.getRegistrationID() != null) {
				param.put("registrationID", iuser.getRegistrationID());
			}
			String rvdString = https_get(auto_login_url, param);
			MyApplication.setSessionTime(new Date());
			if (rvdString != null) {
				PUser user = JSONUtil.getJson2Entity(rvdString, PUser.class);
				if (user != null && user.getData() != null && user.getCode() == SUCCESS_CODE) {
					rvd = new RtnValueDto();
					rvd.setUserData(user.getData());
					rvd.setCode(user.getCode());
					if (user.getData() != null && user.getCode() == SUCCESS_CODE) {
						try {
							SUser suser = MyApplication.getInstance().getSqliteService().getScurrentUser();
							if (suser == null) {
								suser = new SUser();
							}
							if (iuser.getRegistrationID() != null) {
								suser.setRegistrationID(iuser.getRegistrationID());
							}
							if(user.getData().getEmail()!=null){
								suser.setEmail(user.getData().getEmail());
							}
							if(user.getData().getMobilephone()!=null){
								suser.setMobilephone(user.getData().getMobilephone());
							}
							if(user.getData().getToken()!=null){
								suser.setToken(user.getData().getToken());
							}
							setLoginUser(suser);
							// 添加本地数据库用户信息
							MyApplication.getInstance().getSqliteService().addUser();
						} catch (Exception e) {
							System.out.println("SQLite出错了！");
							e.printStackTrace();
							System.out.println("SQLite关闭并初始化！");
							MyApplication.getInstance().getSqliteService().closeDB();
							MyApplication.getInstance().setSqliteService(new SQLiteService(context));
						}
					}
				} else {
					MyApplication.getInstance().setCurrentUser(null);
					MyApplication.getInstance().setSessionId(null);
				}
			}
		}
		return rvd;
	}

	/**
	 * 设置登录用户
	 * 
	 * @param user
	 * @param token
	 */
	private static void setLoginUser(SUser user) {
		// 把返回的用户设置成当前用户
		MyApplication.getInstance().setCurrentUser(user);
		
		if (MyApplication.getTb_url() == null || "".equals(MyApplication.getTb_url())) {
			MyApplication.setTb_url(user.getUrl());
		}
	}

	private String https_get(String path, HashMap<String, String> param) {
		String content = null;
		try {
			StringBuffer paramsBuffer = new StringBuffer();
			if (param != null && param.size() > 0) {
				Set<String> set = param.keySet();
				Iterator<String> it = set.iterator();
				int count = 0;
				while (it.hasNext()) {
					String key = it.next();
					String value = param.get(key);
					if (count > 0) {
						paramsBuffer.append("&");
					}
					paramsBuffer.append(key + "=" + URLEncoder.encode(value, "UTF-8"));
					count++;
				}
			}
			String params = paramsBuffer.toString();
			if (params != null && !"".equals(params.trim())) {
				if (path.indexOf("?") == -1) {
					path = path + "?" + params;
				} else if (path.indexOf("?") == path.length() - 1) {
					path = path + params;
				} else {
					path = path + "&" + params;
				}
			}

			// long a = System.currentTimeMillis();
			String key = "222222";
			char[] keys = key.toCharArray();
			KeyStore keyStore = KeyStore.getInstance("BKS");
			InputStream ins = context.getResources().openRawResource(R.raw.client1);
			// long b = System.currentTimeMillis();
			keyStore.load(ins, keys);
			// long c = System.currentTimeMillis();
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
			// long d = System.currentTimeMillis();
			kmf.init(keyStore, keys);
			KeyManager[] keyManagers = kmf.getKeyManagers();
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(keyManagers, new X509TrustManager[] { new NullX509TrustManager() },
					null);
			HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
			HttpURLConnection urlConnection = null;
			InputStream is = null;
			try {
				URL requestedUrl = new URL(path);
				urlConnection = (HttpURLConnection) requestedUrl.openConnection();
				if (urlConnection instanceof HttpsURLConnection) {
					((HttpsURLConnection) urlConnection).setSSLSocketFactory(sslContext.getSocketFactory());
				}
				urlConnection.setRequestMethod("GET");
				urlConnection.setConnectTimeout(10000);
				urlConnection.setReadTimeout(10000);
				
				try {
					is = urlConnection.getInputStream();
					StringBuffer sb = new StringBuffer();
					byte[] bytes = new byte[1024];
					for (int len = 0; (len = is.read(bytes)) != -1;) {
						sb.append(new String(bytes, 0, len, "utf-8"));
					}
					content = sb.toString();
					/* 获取 sessionid begin */
					String cookieval = urlConnection.getHeaderField("set-cookie");
					String sessionid;
					if (cookieval != null) {
						sessionid = cookieval.substring(0, cookieval.indexOf(";"));
						MyApplication.getInstance().setSessionId(sessionid);
					}
					/* 获取 sessionid end */
				} catch (ConnectException e) {
					content = CodeValidator.getErrorNetworkCodeResult();
				}
			} catch (Exception ex) {
				content = CodeValidator.getErrorNetworkCodeResult();
				ex.printStackTrace();
			} finally {
				if (urlConnection != null) {
					urlConnection.disconnect();
				}
				if(is != null){
					is.close();
				}
			}
			// long e = System.currentTimeMillis();
			// long b_a = b - a;// 2
			// long c_b = c - b;// 196
			// long d_c = d - c;// 0
			// long e_d = e - d;// 4525
			// long e_a = e - a;// 4723
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}

	public RtnValueDto register(SUser iuser) {
		if(CodeValidator.isNetworkError()){
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto rvd = null;
		if (iuser != null && iuser.getUsername() != null && iuser.getToken() != null) {
			HashMap<String, String> param = new HashMap<String, String>();
			param.put("username", iuser.getUsername());
			param.put("token", iuser.getToken());
			String rvdString = https_get(auto_login_url, param);
			if (rvdString != null) {
				PUser user = JSONUtil.getJson2Entity(rvdString, PUser.class);
				if (user != null && user.getData() != null && user.getCode() == SUCCESS_CODE) {
					rvd = new RtnValueDto();
					rvd.setUserData(user.getData());
					rvd.setCode(200000);
					setLoginUser(user.getData());
					try {
						// 添加本地数据库用户信息
						MyApplication.getInstance().getSqliteService().addUser();
					} catch (Exception e) {
						System.out.println("SQLite出错了！");
						e.printStackTrace();
						System.out.println("SQLite关闭并初始化！");
						MyApplication.getInstance().getSqliteService().closeDB();
						MyApplication.getInstance().setSqliteService(new SQLiteService(context));
					}
				}
			}
		}
		return rvd;
	}
}
