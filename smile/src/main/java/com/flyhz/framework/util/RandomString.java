
package com.flyhz.framework.util;

import java.security.SecureRandom;

public class RandomString {

	/**
	 * 每位允许的字符
	 */
	private static final String	POSSIBLE_CHARS	= "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

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
}
