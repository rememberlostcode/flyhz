
package com.flyhz.framework.view.multipart;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.servlet.ServletRequestContext;

public class LostServletRequestContext extends ServletRequestContext {

	private String	id;

	public LostServletRequestContext(HttpServletRequest request) {
		super(request);
		this.id = request.getSession().getId();
	}

	public String getId() {
		return id;
	}

}
