
package com.flyhz.framework.repository.file;

import java.util.Map;

import com.flyhz.framework.lang.BusinessException;

public class FileUploadCancelException extends BusinessException {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 5531110198949447049L;

	public FileUploadCancelException(Map<String, String> errorFieldMessage) {
		super(errorFieldMessage);
		// TODO Auto-generated constructor stub
	}

	public FileUploadCancelException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public FileUploadCancelException(String message, Map<String, String> errorFieldMessage) {
		super(message, errorFieldMessage);
		// TODO Auto-generated constructor stub
	}

}
