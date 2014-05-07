
package com.flyhz.avengers.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpUtil {

	static final Properties	_CONTENTTYPES	= new Properties();
	static {
		try {
			_CONTENTTYPES.load(ClassLoader.getSystemResourceAsStream("contentType.properties"));
		} catch (IOException e) {
			throw new RuntimeException("contentType.properties is not in the classpath", e);
		}

	}
	static final String		ENCODING		= "UTF-8";

	/**
	 * 
	 * @param request
	 * @param response
	 * @param encoding
	 * @param fileName
	 * @param fileType
	 * @param fileLength
	 * @throws IOException
	 */
	public static void setHttpHeaderWithDownload(HttpServletRequest request,
			HttpServletResponse response, String encoding, String fileName, String fileType,
			Long fileLength) throws IOException {
		if (StringUtil.isEmpty(encoding)) {
			encoding = ENCODING;
		}
		if (StringUtil.isEmpty(fileName)) {
			throw new RuntimeException("fileName can't be null");
		}
		if (StringUtil.isEmpty(fileType)) {
			throw new RuntimeException("fileType can't be null");
		}
		response.setContentType("text/html;charset=" + encoding);
		request.setCharacterEncoding(encoding);
		String contentType = _CONTENTTYPES.getProperty(fileType);
		if (StringUtil.isEmpty(contentType)) {
			contentType = _CONTENTTYPES.getProperty("*");
		}
		response.setContentType(contentType + ";charset=" + encoding);
		response.setCharacterEncoding(encoding);
		Browser browser = getClientBrowser(request);
		if (Browser.IE7.equals(browser) || Browser.IE6.equals(browser)
				|| Browser.IE9.equals(browser) || Browser.IE8.equals(browser)) {
			response.setHeader("Content-disposition",
					"attachment; filename=\"" + URLEncoder.encode(fileName, encoding) + "\"");
		} else if (Browser.FIREFOX.equals(browser)) {
			response.setHeader("Content-disposition", "attachment; filename*=UTF-8'zh_cn'"
					+ URLEncoder.encode(fileName, encoding));
		} else if (Browser.SAFARI.equals(browser)) {
			response.setHeader("Content-disposition", "attachment; filename=\""
					+ new String(fileName.getBytes(), "ISO-8859-1") + "\"");
		} else {
			response.setHeader("Content-disposition",
					"attachment; filename=\"" + URLEncoder.encode(fileName, encoding) + "\"");
		}

		response.setHeader("Content-Length", String.valueOf(fileLength));

	}

	static void writeToResponse(HttpServletResponse response, BufferedInputStream bis)
			throws IOException {
		java.io.BufferedOutputStream bos = null;

		bos = new BufferedOutputStream(response.getOutputStream());
		byte[] buff = new byte[4096];
		int bytesRead;
		while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
			bos.write(buff, 0, bytesRead);
		}
	}

	public void download(HttpServletRequest request, HttpServletResponse response, String encoding,
			String fileName, String fileType, Long fileLength, File file) throws IOException {
		if (file == null) {
			throw new RuntimeException("file cant't be null");
		}
		java.io.BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		setHttpHeaderWithDownload(request, response, encoding, fileName, fileType, fileLength);
		writeToResponse(response, bis);
	}

	public void download(HttpServletRequest request, HttpServletResponse response, String encoding,
			String fileName, String fileType, Long fileLength, BufferedInputStream bis)
			throws IOException {
		if (bis == null) {
			throw new RuntimeException("bis cant't be null");
		}
		setHttpHeaderWithDownload(request, response, encoding, fileName, fileType, fileLength);
		writeToResponse(response, bis);
	}

	public enum Browser {
		IE9("MSIE 9.0"), IE8("MSIE 8.0"), IE7("MSIE 7.0"), IE6("MSIE 6.0"), MAXTHON("Maxthon"), QQ(
				"QQBrowser"), GREEN("GreenBrowser"), SE360("360SE"), FIREFOX("Firefox"), OPERA(
				"Opera"), CHROME("Chrome"), SAFARI("Safari"), OTHER("其它");
		public String	value;

		private Browser(String value) {
			this.value = value;
		}
	}

	public static Browser checkBrowser(String userAgent) {
		if (regex(Browser.OPERA.value, userAgent))
			return Browser.OPERA;
		if (regex(Browser.CHROME.value, userAgent))
			return Browser.CHROME;
		if (regex(Browser.FIREFOX.value, userAgent))
			return Browser.FIREFOX;
		if (regex(Browser.SAFARI.value, userAgent))
			return Browser.SAFARI;
		if (regex(Browser.SE360.value, userAgent))
			return Browser.SE360;
		if (regex(Browser.GREEN.value, userAgent))
			return Browser.GREEN;
		if (regex(Browser.QQ.value, userAgent))
			return Browser.QQ;
		if (regex(Browser.MAXTHON.value, userAgent))
			return Browser.MAXTHON;
		if (regex(Browser.IE9.value, userAgent))
			return Browser.IE9;
		if (regex(Browser.IE8.value, userAgent))
			return Browser.IE8;
		if (regex(Browser.IE7.value, userAgent))
			return Browser.IE7;
		if (regex(Browser.IE6.value, userAgent))
			return Browser.IE6;
		return Browser.OTHER;
	}

	static boolean regex(String regex, String str) {
		Pattern p = Pattern.compile(regex, Pattern.MULTILINE);
		Matcher m = p.matcher(str);
		return m.find();
	}

	public static Browser getClientBrowser(HttpServletRequest request) {
		String Agent = request.getHeader("User-Agent");
		return checkBrowser(Agent);
	}

	public static String getClientIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip != null) {
			String[] ips = ip.split(",");
			for (int i = 0; i < ips.length; i++) {
				if (null != ips[i] && !"unknown".equalsIgnoreCase(ips[i])) {
					ip = ips[i];
					return ip;
				}
			}
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
}
