
package com.flyhz.framework.lang.multipart;

import java.util.Map;

public class FileUploadValidateStatus {

	private String							error;

	private Map<String, FileUploadStatus[]>	map;

	public FileUploadValidateStatus(String error, Map<String, FileUploadStatus[]> map) {
		super();
		this.error = error;
		this.map = map;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Map<String, FileUploadStatus[]> getMap() {
		return map;
	}

	public void setMap(Map<String, FileUploadStatus[]> map) {
		this.map = map;
	}
}
