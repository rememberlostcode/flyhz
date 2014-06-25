
package com.holding.smile.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrUtils {

	/**
	 * 判断字符串是否为空
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isNotEmpty(String s) {
		if (s != null && !"".equals(s.trim()))
			return true;
		return false;
	}

	/**
	 * 验证密码负责度，必须同时有字母和数字
	 * 
	 * @param password
	 * @return
	 */
	public static boolean chaeckPassword(String password) {
		boolean includeNum = false;// 包含字母
		boolean includeLetter = false;// 包含字母
		// boolean includeOther = false;//包含其它字符
		for (int i = 0; i < password.length(); i++) {
			char c = password.charAt(i);
			if (c >= '0' && c <= '9') {
				includeNum = true;
			} else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
				includeLetter = true;
			}
			// else {
			// includeOther = true;
			// }
		}

		return includeNum && includeLetter;// && includeOther;
	}

	/**
	 * 验证手机号码
	 * 
	 * @param phoneNumber
	 * @return
	 */
	public static boolean checkPhoneNumber(String phoneNumber) {
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9])|(17[0-9]))\\d{8}$");
		Matcher m = p.matcher(phoneNumber);
		return m.matches();
	}

	/**
	 * 验证邮箱
	 * 
	 * @param email
	 * @return
	 */
	public static boolean checkEmail(String email) {
		boolean flag = false;
		try {
			String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
	
	public static void main(String[] args){
		System.out.println(checkEmail("hd1111@sina.com"));
	}
}
