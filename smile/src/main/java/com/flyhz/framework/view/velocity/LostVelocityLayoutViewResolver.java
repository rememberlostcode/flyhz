
package com.flyhz.framework.view.velocity;

import java.util.Locale;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import com.flyhz.framework.FinderConfig;
import com.flyhz.framework.util.StringUtil;

public class LostVelocityLayoutViewResolver extends UrlBasedViewResolver implements
		InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {
		FinderConfig lostWebCoreConfig = BeanFactoryUtils.beanOfTypeIncludingAncestors(
				getApplicationContext(), FinderConfig.class, true, false);

		String suffix = (String) lostWebCoreConfig.getConfig(FinderConfig.SPRING_URL_BASED_VIEW_RESOLVER_SUFFIX);
		if (!StringUtil.isEmpty(suffix)) {
			super.setSuffix(suffix);
		}
	}

	/**
	 * Requires LostVelocityLayoutView.
	 * 
	 * @see LostVelocityLayoutView
	 */
	@Override
	protected Class<?> getViewClass() {
		return LostVelocityLayoutView.class;
	}

	@Override
	protected AbstractUrlBasedView buildView(String viewName) throws Exception {
		LostVelocityLayoutView view = (LostVelocityLayoutView) super.buildView(viewName);
		view.setEncoding("UTF-8");
		view.setUrl(viewName + getSuffix());
		view.setLayoutUrl(viewName + ".layout" + getSuffix());
		return view;
	}

	@Override
	public View resolveViewName(String viewName, Locale locale) throws Exception {
		return super.resolveViewName(viewName, locale);
	}
}
