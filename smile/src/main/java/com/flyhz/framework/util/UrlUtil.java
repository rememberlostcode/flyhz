
package com.flyhz.framework.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * 
 * 类说明：url工具类
 * 
 * @author robin 2014-3-10下午2:41:02
 * 
 */
public class UrlUtil {

	/**
	 * 功能：判断并检测当前URL是否可连接或是否有效, 最多连接网络 3 次, 如果 3 次都不成功，视为该地址不可用
	 * 
	 * @param urlStr
	 *            指定URL网络地址
	 * @return URL
	 */
	public static boolean isConnect(String urlStr) {
		int counts = 0;
		boolean retu = false;
		if (urlStr == null || urlStr.length() <= 0) {
			return retu;
		}

		if (urlStr.indexOf("http://") < 0) {
			urlStr = "http://" + urlStr;
		}

		// 验证是否是链接
		if (!RegexUtils.checkURL(urlStr) && !RegexUtils.checkIpAddress(urlStr)) {
			return retu;
		}

		while (counts < 3) {
			try {
				URL url = new URL(urlStr);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				int state = con.getResponseCode();
				if (state == 200) {
					retu = true;
					break;
				}
			} catch (Exception ex) {
				counts++;
				continue;
			}
		}
		return retu;
	}
	
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
//			// 获取所有响应头字段
//			Map<String, List<String>> map = connection.getHeaderFields();
//			// 遍历所有的响应头字段
//			for (String key : map.keySet()) {
//				// System.out.println(key + "--->" + map.get(key));
//			}
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

	public static void main(String[] args) {
		boolean res = isConnect("www.baidu.com");
		System.out.println(res);

		// res = isConnect("www.google.com");
		// System.out.println(res);

		res = isConnect("zuir2.zju.edu.cn");
		System.out.println(res);

		res = isConnect("zuir3.zju.edu.cn");
		System.out.println(res);

		res = isConnect("10.202.37.11");
		System.out.println(res);

		System.exit(0);
	}

}
