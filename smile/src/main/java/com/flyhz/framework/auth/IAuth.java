
package com.flyhz.framework.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class IAuth implements Authenticate {

	public static final String	UID	= "u";

	@Override
	public Integer recognize(HttpServletRequest request, HttpServletResponse response) {
		HttpSession httpSession = request.getSession();
		if (httpSession != null) {
			Object obj = httpSession.getAttribute(UID);
			if (obj != null) {
				return (Integer) obj;
			}
		}
		return null;
	}

	@Override
	public void mark(Integer id, HttpServletRequest request, HttpServletResponse response) {
		if (id != null) {
			HttpSession httpSession = request.getSession();
			httpSession.setAttribute(UID, id);
		}
	}

	@Override
	public void removeMark(HttpServletRequest request, HttpServletResponse response) {
		HttpSession httpSession = request.getSession();
		if (httpSession != null) {
			httpSession.removeAttribute(UID);
		}
	}

}
