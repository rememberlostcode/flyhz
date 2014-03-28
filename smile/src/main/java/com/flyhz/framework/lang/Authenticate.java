
package com.flyhz.framework.lang;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.flyhz.framework.id.ID;
import com.flyhz.framework.id.WebUser;

public interface Authenticate {

	public WebUser identify(HttpServletRequest request, HttpServletResponse response);

	public String display(ID id);

	public ID login(HttpServletRequest request, HttpServletResponse response)
			throws BusinessException;

	public void logout(HttpServletRequest request, HttpServletResponse response);
}
