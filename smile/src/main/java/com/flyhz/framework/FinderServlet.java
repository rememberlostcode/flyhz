
package com.flyhz.framework;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;

import com.flyhz.framework.auth.WebUser;
import com.flyhz.framework.lang.page.Pager;

public class FinderServlet extends DispatcherServlet {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -5218643358161513909L;

	public FinderServlet() {
		setContextConfigLocation("");
	}

	@Override
	public void setContextConfigLocation(String contextConfigLocation) {
		super.setContextConfigLocation("classpath:/spring/finder-servlet-context.xml");
	}

	@Override
	protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		super.render(mv, request, response);

	}

	@Override
	protected void doService(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		super.doService(request, response);
		WebUser.removeCurrentWebUser();
		Pager.removeCurrentPager();
	}
}
