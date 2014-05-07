
package com.flyhz.avengers.util;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 校验工具类
 * 
 * @author fuwb 20131227
 */
public class ValidateUtil {
	/**
	 * 验证邮箱地址是否正确
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

	/**
	 * 验证手机号码
	 * 
	 * @param mobiles
	 * @return [0-9]{5,9}
	 */
	public static boolean isMobileNO(String mobiles) {
		boolean flag = false;
		try {
			Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
			Matcher m = p.matcher(mobiles);
			flag = m.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 校验邮编是否正确
	 * 
	 * @author fuwb
	 * @param zipcode
	 * @return boolean
	 */
	public static boolean isValidZipcode(String zipcode) {
		boolean flag = false;
		try {
			Pattern p = Pattern.compile("^\\d{6}$");
			Matcher m = p.matcher(zipcode);
			flag = m.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 校验经度值是否有效(经度值范围-180~180;东经为正数，西经为负数)
	 * 
	 * @param lng
	 * @return boolean
	 */
	public static boolean isValidLng(Double lng) {
		if (lng != null) {
			BigDecimal b1 = new BigDecimal(180);
			BigDecimal b2 = new BigDecimal(-180);
			BigDecimal param = new BigDecimal(lng);
			return param.compareTo(b1) <= 0 && param.compareTo(b2) >= 0;
		}
		return false;
	}

	/**
	 * 校验纬度值是否有效(纬度值范围-90~90;北纬为正数，南纬为负数)
	 * 
	 * @param lat
	 * @return boolean
	 */
	public static boolean isValidLat(Double lat) {
		if (lat != null) {
			BigDecimal b1 = new BigDecimal(90);
			BigDecimal b2 = new BigDecimal(-90);
			BigDecimal param = new BigDecimal(lat);
			return param.compareTo(b1) <= 0 && param.compareTo(b2) >= 0;
		}
		return false;
	}

	public static void main(String[] args) {
		System.out.println(isValidLat(null));
	}
}
