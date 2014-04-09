
package com.holding.smile.tools;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class URLUtil {

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
}
