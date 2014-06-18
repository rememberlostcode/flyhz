
package com.flyhz.avengers.framework.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class StringUtil extends StringUtils {

	public static final String	EMPTY_STRING	= "";
	public static final String	NULL_STRING		= "null";

	/**
	 * 将单词的首字母大写
	 */
	public static String firstLetterToUpperCase(String word) {
		return word.substring(0, 1).toUpperCase() + word.substring(1, word.length());
	}

	/**
	 * 将","分割的字符串转换成String集合
	 * 
	 * @param <T>
	 * @param str
	 * @return
	 */
	public static Collection<String> convertStringToStringCollection(String str) {
		if (str != null) {
			HashSet<String> set = new HashSet<String>();
			String[] strArray = str.split(",");
			if (strArray != null && strArray.length > 0) {
				for (String s : strArray) {
					set.add(s);
				}
				return set;
			}
		}
		return new HashSet<String>();

	}

	/**
	 * 将","分割的字符串转换成Integer集合
	 * 
	 * @param <T>
	 * @param str
	 * @return
	 */
	public static Collection<Integer> convertStringToIntegerCollection(String str) {
		if (str != null) {
			LinkedHashSet<Integer> set = new LinkedHashSet<Integer>();
			String[] strArray = str.split(",");
			if (strArray != null && strArray.length > 0) {
				for (String s : strArray) {
					set.add(Integer.valueOf(s));
				}
				return set;
			}
		}
		return new LinkedHashSet<Integer>();

	}

	/**
	 * 将集合转换成","分割的字符串
	 * 
	 * @param <T>
	 * @param str
	 * @return
	 */
	public static <T> String convertCollectionToString(Collection<T> collection) {
		StringBuffer sb = new StringBuffer();
		if (collection != null && collection.size() > 0) {
			for (T o : collection) {
				sb.append(o).append(",");
			}
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();

	}

	/**
	 * 将","分割的字符串转换成Long集合
	 * 
	 * @param <T>
	 * @param str
	 * @return
	 */
	public static Collection<Long> convertStringToLongCollection(String str) {
		if (str != null) {
			HashSet<Long> set = new HashSet<Long>();
			String[] strArray = str.split(",");
			if (strArray != null && strArray.length > 0) {
				for (String s : strArray) {
					set.add(Long.valueOf(s));
				}
				return set;
			}
		}
		return new HashSet<Long>();

	}

	public static boolean equals(String str1, String str2) {
		if (str1 == null) {
			return str1 == str2;
		}
		return str1.equals(str2);
	}

	public static String assembleGetMethodName(String fieldName) {
		if (!StringUtil.isEmpty(fieldName)) {
			StringBuffer sb = new StringBuffer();
			sb.append("get");
			sb.append(fieldName.substring(0, 1).toUpperCase());
			sb.append(fieldName.substring(1));
			return sb.toString();
		}
		return StringUtil.EMPTY_STRING;
	}

	public static boolean compare(String str) {
		return "Y".equals(str) || "y".equals(str) || "true".equals(str) || "1".equals(str);
	}

	public static boolean compare(Character c) {
		return Character.valueOf('Y').equals(c) || Character.valueOf('y').equals(c)
				|| Character.valueOf('1').equals(c);
	}

	public static final String	DEFAULT_DATE_FORMAT	= "yyyy-MM-dd hh:mm:ss";

	public static Date convertStringToDate(String strDate, String format) {
		DateFormat f = new SimpleDateFormat(format);
		try {
			return f.parse(strDate);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 将集合内的对象拼成字符串，每个对象的直接调用toString()方法形成字符串,每个对象已$delimiter分隔
	 * 
	 * @param list
	 * @param delimiter
	 */
	public static StringBuffer assembleStrBufferByList(String delimiter, Object... objects) {
		if (objects != null && objects.length > 0) {
			StringBuffer sb = new StringBuffer();
			for (Object object : objects) {
				sb.append(object).append(delimiter);
			}
			if (sb.length() > 0) {
				sb.deleteCharAt(sb.length() - 1);
			}
			return sb;
		}
		return null;
	}

	/**
	 * 获得byte的二进制码
	 * 
	 * @param name
	 */
	public static String getBinaryStr(Byte b) {
		if (b != null) {
			return Integer.toBinaryString(b.intValue());
		}
		return null;

	}

	public static String toMD5Str(String str) {
		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance("MD5");

			messageDigest.reset();

			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException caught!");
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		byte[] byteArray = messageDigest.digest();

		StringBuffer md5StrBuff = new StringBuffer();

		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}

		return md5StrBuff.toString();
	}

	/**
	 * 获取字符串的长度，中文占两个字符,英文数字占一个字符
	 * 
	 * @param value
	 *            指定的字符串
	 * @return 字符串的长度
	 */
	public static double stringLength(String value) {
		if (value == null)
			return 0;

		double valueLength = 0;
		String chinese = "[\u4e00-\u9fa5]";
		// 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
		for (int i = 0; i < value.length(); i++) {
			// 获取一个字符
			String temp = value.substring(i, i + 1);
			// 判断是否为中文字符
			if (temp.matches(chinese)) {
				// 中文字符长度为2
				valueLength += 2;
			} else {
				// 其他字符长度为1
				valueLength += 1;
			}
		}
		// 进位取整
		return Math.ceil(valueLength);
	}

	/**
	 * 替换字符串
	 * 
	 * @param str
	 *            字符串
	 * @param replaceChar
	 *            替换的原始字符
	 * @param targetChar
	 *            目标字符
	 * @return
	 */
	public String replaceAllStr(String str, String replaceChar, String targetChar) {
		if (StringUtils.isNotBlank(str))
			str = str.replaceAll(replaceChar, targetChar);
		return str;
	}

	/**
	 * 校验URL是否匹配正则表达式集合
	 * 
	 * @param url
	 * @param values
	 * @return boolean
	 */
	public static boolean filterUrl(String url, List<String> values) {
		boolean flag = false;
		if (StringUtils.isNotBlank(url) && values != null && !values.isEmpty()) {
			for (String value : values) {
				if (StringUtils.isNotBlank(value)) {
					// 校验URL是否匹配正则表达式
					flag = Pattern.compile(value).matcher(url).find();
					if (flag) {
						return flag;
					}
				}
			}
		}
		return flag;
	}

	public static void main(String[] name) {
		// System.out.println(convertStringToDate("2011-06-16 11:30:20",
		// DEFAULT_DATE_FORMAT));
		// System.out.println(200 / 200);
		List<String> values = new ArrayList<String>();
		values.add("^http://www.coach.com/online/handbags/product");
		String url = "http://www.coach.com/online/handbags/product?id=123456";
		System.out.println(filterUrl(url, values));
		url = "http://www.coach.com/online/mens/product?id=123456";
		System.out.println(filterUrl(url, values));
		values.clear();
		values.add("^http://www.abercrombie.com/shop/hk/mens-short-sleeve-graphic-tees/.*\\d+$");
		url = "http://www.abercrombie.com/shop/hk/mens-short-sleeve-graphic-tees/opalescent-river-tee-1871079_01";
		System.out.println(filterUrl(url, values));
		url = "http://www.abercrombie.com/shop/hk/mens-short-sleeve-graphic-tees/opalescent-river-tee";
		System.out.println(filterUrl(url, values));
	}
}
