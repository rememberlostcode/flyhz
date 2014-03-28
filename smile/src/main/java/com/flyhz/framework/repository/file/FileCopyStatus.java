
package com.flyhz.framework.repository.file;

public abstract class FileCopyStatus {

	protected String			id;
	protected volatile boolean	isCancel	= false;
	protected long				bytesRead	= 0;
	protected long				contentLength;

	public FileCopyStatus(String id, long contentLength) {
		this.id = id;
		this.contentLength = contentLength;
	}

	public abstract void update(long bytesRead);

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isCancel() {
		return isCancel;
	}

	public void setCancel(boolean isCancel) {
		this.isCancel = isCancel;
	}

	public long getBytesRead() {
		return bytesRead;
	}

	public void setBytesRead(long bytesRead) {
		this.bytesRead = bytesRead;
	}

	public long getContentLength() {
		return contentLength;
	}

	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}
}
