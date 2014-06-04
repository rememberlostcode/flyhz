
package com.flyhz.framework.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Authenticate {
	public void mark(Integer id, HttpServletRequest request, HttpServletResponse response);

	public Integer recognize(HttpServletRequest request, HttpServletResponse response);

	public void removeMark(HttpServletRequest request, HttpServletResponse response);

}
