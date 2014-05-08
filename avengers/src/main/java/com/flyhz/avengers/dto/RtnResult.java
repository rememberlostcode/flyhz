
package com.flyhz.avengers.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 类说明：通用返回结果类
 * 
 * @author robin 2014-5-8下午4:37:32
 * 
 */
public class RtnResult {
	private String				siteName;								// 网站名称
	private String				result;								// json字符串
	private Map<String, String>	map	= new HashMap<String, String>();
	private String				dataType;								// 数据类型

}
