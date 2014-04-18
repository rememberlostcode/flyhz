
package com.holding.smile.entity;

import java.io.File;
import java.io.Serializable;

public class IdentitycardDto implements Serializable {

	private static final long	serialVersionUID	= -6318242080957337047L;

	private Integer				id;

	private File				file;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

}