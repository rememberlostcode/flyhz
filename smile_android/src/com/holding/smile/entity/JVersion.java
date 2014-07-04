
package com.holding.smile.entity;

import java.io.Serializable;

public class JVersion implements Serializable {

	private static final long	serialVersionUID	= 6573394747310499289L;
	private String				versionNew;
	private String				versionApk;
	private String				versionLog;

	public String getVersionNew() {
		return versionNew;
	}

	public void setVersionNew(String versionNew) {
		this.versionNew = versionNew;
	}

	public String getVersionApk() {
		return versionApk;
	}

	public void setVersionApk(String versionApk) {
		this.versionApk = versionApk;
	}

	public String getVersionLog() {
		return versionLog;
	}

	public void setVersionLog(String versionLog) {
		this.versionLog = versionLog;
	}
}
