
package com.holding.smile.dto;

/**
 * 业务校验结果DTO
 * 
 * @author robin 2013-12-28 上午11:26:37
 * 
 */
public class ValidateDto {
	private String	option	= "0";	// 处理方式 0:不操作，1:单条记录刷新,2:单条记录删除，3：清屏
	private String	message;		// 错误信息

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
