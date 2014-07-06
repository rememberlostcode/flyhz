
package com.flyhz.avengers.framework.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.net.util.Base64;

import com.flyhz.avengers.framework.config.XConfiguration;

/**
 * 
 * 类说明：url工具类
 * 
 * @author robin 2014-3-10下午2:41:02
 * 
 */
public class URLXConnectionUtil {

	public static HttpURLConnection getXHttpConnection(URL url) throws IOException {
		@SuppressWarnings("unchecked")
		Map<String, Object> xProxy = (Map<String, Object>) XConfiguration.getAvengersContext().get(
				XConfiguration.PROXY);
		if (xProxy.get(XConfiguration.PROXY_USERNAME) != null) {
			final String username = (String) xProxy.get(XConfiguration.PROXY_USERNAME);
			final String password = (String) xProxy.get(XConfiguration.PROXY_PASSWORD);
			if (StringUtil.isNotBlank(username)) {
				Authenticator.setDefault(new Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password.toCharArray());
					}
				});
			}

		}
		String host = (String) xProxy.get(XConfiguration.PROXY_HOST);
		Integer port = (Integer) xProxy.get(XConfiguration.PROXY_PORT);
		SocketAddress addr = new InetSocketAddress(host, port);
		Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
		connection.setRequestProperty("accept", "*/*");
		connection.setRequestProperty("connection", "Keep-Alive");
		connection.setRequestProperty("user-agent",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
		connection.setRequestProperty("Proxy-Authorization",
				"Basic" + new String(Base64.encodeBase64("nuaa:oHL!_ws1256Fs54".getBytes())));
		return (HttpURLConnection) connection;
	}

	/**
	 * 发送get请求
	 * 
	 * @param url
	 *            地址
	 * @return
	 */
	public static String sendGet(String url) {
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url;
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();
			// // 获取所有响应头字段
			// Map<String, List<String>> map = connection.getHeaderFields();
			// // 遍历所有的响应头字段
			// for (String key : map.keySet()) {
			// // System.out.println(key + "--->" + map.get(key));
			// }
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * get请求，参数不经过URLEncoder处理，参数要自己处理好
	 * 
	 * @param path
	 * @param param
	 * @return
	 */
	public static String getStringByGetNotEncod(String path, HashMap<String, String> param) {
		if (path == null || "".equals(path.trim())) {
			return null;
		}
		StringBuffer result = new StringBuffer();
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
					// paramsBuffer.append(key + "=" + URLEncoder.encode(value,
					// "UTF-8"));
					paramsBuffer.append(key + "=" + value);
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
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setConnectTimeout(5000);
			conn.setReadTimeout(3000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.setRequestProperty("Charset", "UTF-8"); // 设置编码
			// conn.setRequestProperty("Cookie",
			// MyApplication.getInstance().getSessionId());
			conn.connect();
			if (conn.getResponseCode() == 200) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						conn.getInputStream(), "UTF-8"));
				String lines;
				while ((lines = reader.readLine()) != null) {
					result.append(lines);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return result.toString();
	}

	public static String getStringByGet(String path, HashMap<String, String> param) {
		if (path == null || "".equals(path.trim())) {
			return null;
		}
		StringBuffer result = new StringBuffer();
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
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setConnectTimeout(5000);
			conn.setReadTimeout(3000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.setRequestProperty("Charset", "UTF-8"); // 设置编码
			// conn.setRequestProperty("Cookie",
			// MyApplication.getInstance().getSessionId());
			conn.connect();
			if (conn.getResponseCode() == 200) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						conn.getInputStream(), "UTF-8"));
				String lines;
				while ((lines = reader.readLine()) != null) {
					result.append(lines);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return result.toString();
	}

	public static String getStringByPost(String path, HashMap<String, String> param) {
		if (path == null || "".equals(path.trim())) {
			return null;
		}
		StringBuffer result = new StringBuffer();
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
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(3000);
			conn.setReadTimeout(3000);
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestProperty("Charset", "UTF-8"); // 设置编码
			// conn.setRequestProperty("Cookie",
			// MyApplication.getInstance().getSessionId());
			conn.connect();
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			out.writeBytes(paramsBuffer.toString());
			out.flush();
			out.close();

			if (conn.getResponseCode() == 200) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						conn.getInputStream(), "UTF-8"));
				String lines;
				while ((lines = reader.readLine()) != null) {
					result.append(lines);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return result.toString();
	}

	public static String getStringByPostMulti(String path, HashMap<String, List<String>> param) {
		if (path == null || "".equals(path.trim())) {
			return null;
		}
		StringBuffer result = new StringBuffer();
		try {
			StringBuffer paramsBuffer = new StringBuffer();
			if (param != null && param.size() > 0) {
				Set<String> set = param.keySet();
				Iterator<String> it = set.iterator();
				int count = 0;
				while (it.hasNext()) {
					String key = it.next();
					if (count > 0) {
						paramsBuffer.append("&");
					}
					List<String> value = param.get(key);
					for (String each : value) {
						paramsBuffer.append(key + "=" + URLEncoder.encode(each, "UTF-8"));
					}
					count++;
				}
			}
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(3000);
			conn.setReadTimeout(3000);
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestProperty("Charset", "UTF-8"); // 设置编码
			// conn.setRequestProperty("Cookie",
			// MyApplication.getInstance().getSessionId());
			conn.connect();
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			out.writeBytes(paramsBuffer.toString());
			out.flush();
			out.close();

			if (conn.getResponseCode() == 200) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						conn.getInputStream(), "UTF-8"));
				String lines;
				while ((lines = reader.readLine()) != null) {
					result.append(lines);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return result.toString();
	}

	public static void main(String[] args) {
		// boolean res = isConnect("www.baidu.com");
		// System.out.println(res);

		// res = isConnect("www.google.com");
		// System.out.println(res);
		//
		// res = isConnect("zuir2.zju.edu.cn");
		// System.out.println(res);
		//
		// res = isConnect("zuir3.zju.edu.cn");
		// System.out.println(res);
		//
		// res = isConnect("10.202.37.11");
		// System.out.println(res);

		// String tt =
		// sendGet("http://download.finance.yahoo.com/d/quotes.csv?e=.csv&f=sl1d1t1&s=USDCNY=x");
		// String[] ss = tt.split(",");
		// for (int i = 0; i < ss.length; i++) {
		// System.out.println(ss[i]);
		// }
		System.exit(0);
	}

}
