
package com.flyhz.framework.util;

import java.security.SecureRandom;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;

public class RandomString {

	/**
	 * 每位允许的字符
	 */
	private static final String		POSSIBLE_CHARS	= "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String		POSSIBLE_NUMBER	= "0123456789";
	private static AtomicInteger	count			= new AtomicInteger(0);

	/**
	 * 生产一个指定长度的随机字符串
	 * 
	 * @param length
	 *            字符串长度
	 * @return
	 */
	private static String generateRandomString(int length) {
		StringBuilder sb = new StringBuilder(length);
		SecureRandom random = new SecureRandom();
		for (int i = 0; i < length; i++) {
			sb.append(POSSIBLE_CHARS.charAt(random.nextInt(POSSIBLE_CHARS.length())));
		}
		return sb.toString();
	}

	public static String generateRandomString16() {
		return generateRandomString(16);
	}

	public static String generateRandomString8() {
		return generateRandomString(8);
	}

	public static String generateRandomNumber6() {
		StringBuilder sb = new StringBuilder(6);
		SecureRandom random = new SecureRandom();
		for (int i = 0; i < 6; i++) {
			sb.append(POSSIBLE_NUMBER.charAt(random.nextInt(POSSIBLE_NUMBER.length())));
		}
		return sb.toString();
	}

	/**
	 * 订单号生成规则:毫秒数*100,拼接count
	 * 
	 * @author fuwb 20140623
	 * @return String
	 */
	public static String generateRandomStringTime() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(System.currentTimeMillis());
		if (count.intValue() > 99) {
			count = new AtomicInteger(0);
		}
		stringBuilder.append(StringUtils.leftPad(String.valueOf(count.getAndIncrement()), 2, '0'));
		return stringBuilder.toString();
	}

	public static String generateRandomStringDate() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(DateUtil.strToDateShort(new Date()));
		if (count.intValue() > 9999) {
			count = new AtomicInteger(0);
		}
		stringBuilder.append(StringUtils.leftPad(count.toString(), 4, '0'));
		return stringBuilder.toString();
	}

	public static void main(String[] args) {
		System.out.println(generateRandomStringTime());
		System.out.println(generateRandomStringTime());
	}
}
