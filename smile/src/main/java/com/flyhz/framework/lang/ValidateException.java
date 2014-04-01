
package com.flyhz.framework.lang;


public class ValidateException extends Exception {

	private static final long	serialVersionUID	= -549672389703914949L;
	private Integer				code;

	public ValidateException(Integer code) {
		this.code = code;
	}

	/**
	 * @param message
	 */
	public ValidateException(String message) {
		super(message);
	}

	public Integer getCode() {
		return code;
	}
}
