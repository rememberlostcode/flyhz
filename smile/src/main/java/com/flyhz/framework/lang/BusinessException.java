
package com.flyhz.framework.lang;

import java.util.Map;

public class BusinessException extends Exception {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -549672389703914949L;
	private Map<String, String>	errorFieldMessage;
	private Integer				code;

	public BusinessException(Integer code) {
		this.code = code;
	}

	/**
	 * @param errorFieldMessage
	 */
	public BusinessException(Map<String, String> errorFieldMessage) {
		super();
		this.errorFieldMessage = errorFieldMessage;
	}

	/**
	 * @param message
	 */
	public BusinessException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param message
	 * @param errorFieldMessage
	 */
	public BusinessException(String message, Map<String, String> errorFieldMessage) {
		super(message);
		this.errorFieldMessage = errorFieldMessage;
	}

	/**
	 * @return the errorFieldMessage
	 */
	public Map<String, String> getErrorFieldMessage() {
		return errorFieldMessage;
	}

	public Integer getCode() {
		return code;
	}
}
