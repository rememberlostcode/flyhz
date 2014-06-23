
package com.flyhz.framework.util;

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class RandomString {

	/**
	 * 每位允许的字符
	 */
	private static final String		POSSIBLE_CHARS	= "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
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

	/**
	 * 订单号生成规则:毫秒数*100,拼接count
	 * 
	 * @author fuwb 20140623
	 * @return String
	 */
	public static String generateRandomStringTime() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(System.currentTimeMillis() * 100);
		if (count.intValue() > 99) {
			count = new AtomicInteger(0);
		}
		stringBuilder.append(count.getAndIncrement());
		return stringBuilder.toString();
	}

	public static void main(String[] args) {
		System.out.println(generateRandomStringTime());
		System.out.println(generateRandomStringTime());
	}
}
