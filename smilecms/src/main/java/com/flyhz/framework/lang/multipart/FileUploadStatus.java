
package com.flyhz.framework.lang.multipart;

import java.util.Date;

import com.flyhz.framework.lang.file.FileCopyStatus;

public class FileUploadStatus extends FileCopyStatus {

	public FileUploadStatus(String id, long contentLength) {
		super(id, contentLength);
	}

	protected boolean	error		= false;
	protected boolean	isComplete	= false;
	protected Date		date;

	@Override
	public void update(long bytesRead) {
		this.bytesRead = bytesRead;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
