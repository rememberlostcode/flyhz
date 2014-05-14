
package com.flyhz.avengers.dto;

import java.util.List;

/**
 * 
 * 类说明：通用返回结果类
 * 
 * @author robin 2014-5-8下午4:37:32
 * 
 */
public class RtnResult {
	private String			siteName;	// 网站名称
	private String			result;	// 返回结果字符串
	private List<String>	list;		// 返回结果列表
	private String			dataType;	// 数据类型:json,array,xml

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

}
