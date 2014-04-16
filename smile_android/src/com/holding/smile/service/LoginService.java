package com.holding.smile.service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import android.content.Context;

import com.holding.smile.R;
import com.holding.smile.activity.MyApplication;
import com.holding.smile.dto.RtnLoginDto;
import com.holding.smile.entity.SUser;
import com.holding.smile.tools.JSONUtil;
import com.holding.smile.tools.NullHostNameVerifier;
import com.holding.smile.tools.NullX509TrustManager;

public class LoginService {

	private Context context;
	private static String login_url;
	private static String auto_login_url;

	public LoginService(Context context) {
		super();
		this.context = context;
		login_url = context.getString(R.string.login_url);
		auto_login_url = context.getString(R.string.auto_login_url);
	}

	/**
	 * 用户名密码登录
	 * 
	 * @param iuser
	 * @return
	 */
	public RtnLoginDto login(SUser iuser) {
		RtnLoginDto rvd = null;
		HashMap<String, String> param = new HashMap<String, String>();
		param.put("username", iuser.getUsername());
		param.put("password", iuser.getPassword());
		String rvdString = https_get(login_url, param);
		if (rvdString != null) {
			rvd = JSONUtil.changeJson2RtnLoginDto(rvdString);
			if (rvd != null && rvd.getCode() != null && rvd.getData() != null
					&& rvd.getCode() == 0) {
				SUser ruser = JSONUtil.getJson2Entity(rvd.getData(),
						SUser.class);
				setLoginUser(ruser);
				try {
					// 添加本地数据库用户信息
					MyApplication.getInstance().getSqliteService().addUser();
				} catch (Exception e) {
					System.out.println("SQLite出错了！");
					e.printStackTrace();
					System.out.println("SQLite关闭并初始化！");
					MyApplication.getInstance().getSqliteService().closeDB();
					MyApplication.getInstance().setSqliteService(
							new SQLiteService(context));
				}
			}
		}
		return rvd;
	}

	public RtnLoginDto autoLogin(SUser iuser) {
		RtnLoginDto rvd = null;
		if (iuser != null && iuser.getUsername() != null && iuser.getToken() != null) {
			HashMap<String, String> param = new HashMap<String, String>();
			param.put("username", iuser.getUsername());
			param.put("token", iuser.getToken());
			String rvdString = https_get(auto_login_url, param);
			if (rvdString != null) {
				rvd = JSONUtil.changeJson2RtnLoginDto(rvdString);
				if (rvd != null && rvd.getCode() != null
						&& rvd.getData() != null && rvd.getCode() == 0) {
					SUser ruser = JSONUtil.getJson2Entity(rvd.getData(),
							SUser.class);
					setLoginUser(ruser);
					try {
						// 更新本地数据库用户信息
						MyApplication.getInstance().getSqliteService()
								.updateUser();
					} catch (Exception e) {
						System.out.println("SQLite出错了！");
						e.printStackTrace();
						System.out.println("SQLite关闭并初始化！");
						MyApplication.getInstance().getSqliteService()
								.closeDB();
						MyApplication.getInstance().setSqliteService(
								new SQLiteService(context));
					}
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
					paramsBuffer.append(key + "="
							+ URLEncoder.encode(value, "UTF-8"));
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

			long a = System.currentTimeMillis();
			String key = "222222";
			char[] keys = key.toCharArray();
			KeyStore keyStore = KeyStore.getInstance("BKS");
			InputStream ins = context.getResources().openRawResource(
					R.raw.client1);
			long b = System.currentTimeMillis();
			keyStore.load(ins, keys);
			long c = System.currentTimeMillis();
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
			long d = System.currentTimeMillis();
			kmf.init(keyStore, keys);
			KeyManager[] keyManagers = kmf.getKeyManagers();
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext
					.init(keyManagers,
							new X509TrustManager[] { new NullX509TrustManager() },
							null);
			HttpsURLConnection
					.setDefaultHostnameVerifier(new NullHostNameVerifier());
			HttpURLConnection urlConnection = null;
			try {
				URL requestedUrl = new URL(path);
				urlConnection = (HttpURLConnection) requestedUrl
						.openConnection();
				if (urlConnection instanceof HttpsURLConnection) {
					((HttpsURLConnection) urlConnection)
							.setSSLSocketFactory(sslContext.getSocketFactory());
				}
				urlConnection.setRequestMethod("GET");
				urlConnection.setConnectTimeout(1500);
				urlConnection.setReadTimeout(1500);
				InputStream is = urlConnection.getInputStream();
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
			} catch (Exception ex) {
				//
			} finally {
				if (urlConnection != null) {
					urlConnection.disconnect();
				}
			}
			long e = System.currentTimeMillis();
			long b_a = b - a;// 2
			long c_b = c - b;// 196
			long d_c = d - c;// 0
			long e_d = e - d;// 4525
			long e_a = e - a;// 4723
			System.out.println("");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}
}
