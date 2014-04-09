
package com.holding.smile.tools;

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

}
