
package com.flyhz.avengers.domains.parser;

import com.flyhz.avengers.domains.dto.RtnResult;

/**
 * 
 * 类说明：抽象接口类
 * 
 * @author robin 2014-5-9下午2:54:04
 * 
 */
public interface BaseParser {
	/**
	 * 解析后返回对象
	 * 
	 * @param url
	 * @return
	 */
	public RtnResult parserContent(String url);

	/**
	 * 数据类型
	 * 
	 * @return
	 */
	public String getDataType();
}
