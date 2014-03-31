
package com.flyhz.framework.view.velocity;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContextException;
import org.springframework.web.servlet.view.AbstractTemplateView;
import org.springframework.web.servlet.view.velocity.VelocityToolboxView;

import com.flyhz.framework.FinderConfig;

/**
 * velocityLayoutView不支持velocity-tool2.0,修改该类方法来支持velocity
 * 
 * @author huoding
 * @since 1.0
 */
public class LostVelocityLayoutView extends AbstractTemplateView {

	private LostVelocityEngine			lostVelocityEngine;

	protected Logger					log							= LoggerFactory.getLogger(getClass());

	private boolean						cacheTemplate				= false;

	/**
	 * The default {@link #setLayoutUrl(String) layout url}.
	 */
	public static final String			DEFAULT_LAYOUT				= "layout_default.vm";

	public static final String			DEFAULT_LAYOUT_DO_NOT_USE	= "layout_do_not_use.vm";

	/**
	 * The default {@link #setLayoutUrl(String) layout url}.
	 */
	public static final String			CUSTOMER_DEFAULT_LAYOUT		= "layout.vm";

	/**
	 * The default {@link #setLayoutKey(String) layout key}.
	 */
	public static final String			DEFAULT_LAYOUT_KEY			= "layout";

	/**
	 * The default {@link #setScreenContentKey(String) screen content key}.
	 */
	public static final String			DEFAULT_SCREEN_CONTENT_KEY	= "screen_content";

	private String						defaultLayoutUrl			= DEFAULT_LAYOUT;

	private String						customerLayoutUrl			= CUSTOMER_DEFAULT_LAYOUT;

	private String						layoutUrl;

	private String						layoutKey					= DEFAULT_LAYOUT_KEY;

	private String						screenContentKey			= DEFAULT_SCREEN_CONTENT_KEY;

	private String						doNotUseLayoutValue			= "none";
	public static final AtomicInteger	count						= new AtomicInteger(0);

	private String						encoding;

	private Template					template;

	public LostVelocityLayoutView() {

	}

	/**
	 * Invoked on startup. Looks for a single LostVelocityConfig bean to find
	 * the relevant LostVelocityEngine for this factory.
	 */
	@Override
	protected void initApplicationContext() throws BeansException {
		super.initApplicationContext();
		if (getLostVelocityEngine() == null) {
			setLostVelocityEngine(autodetectVelocityEngine());
		}
	}

	public LostVelocityEngine getLostVelocityEngine() {
		return lostVelocityEngine;
	}

	public void setLostVelocityEngine(LostVelocityEngine lostVelocityEngine) {
		this.lostVelocityEngine = lostVelocityEngine;
	}

	/**
	 * Autodetect a VelocityEngine via the ApplicationContext. Called if no
	 * explicit VelocityEngine has been specified.
	 * 
	 * @return the VelocityEngine to use for VelocityViews
	 * @throws BeansException
	 *             if no VelocityEngine could be found
	 * @see #getApplicationContext
	 * @see #setVelocityEngine
	 */
	protected LostVelocityEngine autodetectVelocityEngine() throws BeansException {
		try {
			FinderConfig lostWebCoreConfig = BeanFactoryUtils.beanOfTypeIncludingAncestors(
					getApplicationContext(), FinderConfig.class, true, false);
			return (LostVelocityEngine) lostWebCoreConfig.getConfig(FinderConfig.LOST_VELOCITY);
		} catch (NoSuchBeanDefinitionException ex) {
			throw new ApplicationContextException(
					"Must define a single LostVelocityConfig bean in this web application context "
							+ "(may be inherited): LostVelocityConfigurer is the usual implementation. "
							+ "This bean may be given any name.", ex);
		}
	}

	public String getDefaultLayoutUrl() {
		return defaultLayoutUrl;
	}

	public void setDefaultLayoutUrl(String layoutPath) {
		this.defaultLayoutUrl = layoutPath;
	}

	public String getLayoutUrl() {
		return layoutUrl;
	}

	public void setLayoutUrl(String layoutUrl) {
		this.layoutUrl = layoutUrl;
	}

	public String getLayoutKey() {
		return layoutKey;
	}

	public void setLayoutKey(String layoutKey) {
		this.layoutKey = layoutKey;
	}

	public String getScreenContentKey() {
		return screenContentKey;
	}

	public void setScreenContentKey(String screenContentKey) {
		this.screenContentKey = screenContentKey;
	}

	/**
	 * Process the model map by merging it with the Velocity template. Output is
	 * directed to the servlet response.
	 * <p>
	 * This method can be overridden if custom behavior is needed.
	 */
	@Override
	protected void renderMergedTemplateModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Context velocityContext = createVelocityContext(model, request, response);

		doRender(velocityContext, response);
	}

	/**
	 * Render the Velocity view to the given response, using the given Velocity
	 * context which contains the complete template model to use.
	 * <p>
	 * The default implementation renders the template specified by the "url"
	 * bean property, retrieved via <code>getTemplate</code>. It delegates to
	 * the <code>mergeTemplate</code> method to merge the template instance with
	 * the given Velocity context.
	 * <p>
	 * Can be overridden to customize the behavior, for example to render
	 * multiple templates into a single view.
	 * 
	 * @param context
	 *            the Velocity context to use for rendering
	 * @param response
	 *            servlet response (use this to get the OutputStream or Writer)
	 * @throws Exception
	 *             if thrown by Velocity
	 * @see #setUrl
	 * @see #getTemplate()
	 * @see #mergeTemplate
	 */
	protected void doRender(Context context, HttpServletResponse response) throws Exception {
		renderScreenContent(context);

		// Velocity context now includes any mappings that were defined
		// (via #set) in screen content template.
		// The screen template can overrule the layout by doing
		// #set( $layout = "MyLayout.vm" )
		String layoutUrlToUse = (String) context.get(this.layoutKey);
		if (layoutUrlToUse != null) {
			log.debug("Screen content template has requested layout [{}]", layoutUrlToUse);
			if (doNotUseLayoutValue.equals(layoutUrlToUse)) {
				layoutUrlToUse = DEFAULT_LAYOUT_DO_NOT_USE;
			}
		} else {
			// No explicit layout URL given -> use default layout of this view.
			if (this.lostVelocityEngine.getVelocityEngine().resourceExists(this.layoutUrl)) {
				layoutUrlToUse = this.layoutUrl;
			} else {
				int lastSlashIndex = getUrl().lastIndexOf("/");
				boolean isCustomerLayoutUrlExists = false;
				if (lastSlashIndex > 0) {
					String customerLayoutUrl = getUrl().substring(0, lastSlashIndex + 1)
							+ "layout.vm";
					if (this.lostVelocityEngine.getVelocityEngine().resourceExists(
							customerLayoutUrl)) {
						isCustomerLayoutUrlExists = true;
						layoutUrlToUse = customerLayoutUrl;
					}
				}
				if (!isCustomerLayoutUrlExists) {

					if (this.lostVelocityEngine.getVelocityEngine().resourceExists(
							this.customerLayoutUrl)) {
						layoutUrlToUse = this.customerLayoutUrl;
					} else {
						layoutUrlToUse = this.defaultLayoutUrl;
					}
				}
			}
		}

		mergeTemplate(getTemplate(layoutUrlToUse), context, response);
	}

	/**
	 * The resulting context contains any mappings from render, plus screen
	 * content.
	 */
	private void renderScreenContent(Context velocityContext) throws Exception {

		log.debug("Rendering screen content template [{}]", getUrl());

		StringWriter sw = new StringWriter();
		Template screenContentTemplate = getTemplate();
		screenContentTemplate.merge(velocityContext, sw);
		// Put rendered content into Velocity context.
		velocityContext.put(this.screenContentKey, sw.toString());
	}

	/**
	 * Retrieve the Velocity template to be rendered by this view.
	 * <p>
	 * By default, the template specified by the "url" bean property will be
	 * retrieved: either returning a cached template instance or loading a fresh
	 * instance (according to the "cacheTemplate" bean property)
	 * 
	 * @return the Velocity template to render
	 * @throws Exception
	 *             if thrown by Velocity
	 * @see #setUrl
	 * @see #setCacheTemplate
	 * @see #getTemplate(String)
	 */
	protected Template getTemplate() throws Exception {
		// We already hold a reference to the template, but we might want to
		// load it
		// if not caching. Velocity itself caches templates, so our ability to
		// cache templates in this class is a minor optimization only.
		if (isCacheTemplate() && this.template != null) {
			return this.template;
		} else {
			return getTemplate(getUrl());
		}
	}

	/**
	 * Return whether the Velocity template should be cached.
	 */
	protected boolean isCacheTemplate() {
		return this.cacheTemplate;
	}

	/**
	 * Create a Velocity Context instance for the given model, to be passed to
	 * the template for merging.
	 * <p>
	 * The default implementation delegates to
	 * {@link #createVelocityContext(Map)}. Can be overridden for a special
	 * context class, for example ChainedContext which is part of the view
	 * package of Velocity Tools. ChainedContext is needed for initialization of
	 * ViewTool instances.
	 * <p>
	 * Have a look at {@link VelocityToolboxView}, which pre-implements
	 * ChainedContext support. This is not part of the standard VelocityView
	 * class in order to avoid a required dependency on the view package of
	 * Velocity Tools.
	 * 
	 * @param model
	 *            the model Map, containing the model attributes to be exposed
	 *            to the view
	 * @param request
	 *            current HTTP request
	 * @param response
	 *            current HTTP response
	 * @return the Velocity Context
	 * @throws Exception
	 *             if there's a fatal error while creating the context
	 * @see #createVelocityContext(Map)
	 * @see #initTool
	 * @see org.apache.velocity.tools.view.context.ChainedContext
	 * @see VelocityToolboxView
	 */
	protected Context createVelocityContext(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return this.lostVelocityEngine.createContext(model, request, response);
	}

	/**
	 * Merge the template with the context. Can be overridden to customize the
	 * behavior.
	 * 
	 * @param template
	 *            the template to merge
	 * @param context
	 *            the Velocity context to use for rendering
	 * @param response
	 *            servlet response (use this to get the OutputStream or Writer)
	 * @throws IOException
	 * @throws Exception
	 *             if thrown by Velocity
	 * @see org.apache.velocity.Template#merge
	 */
	protected void mergeTemplate(Template template, Context context, HttpServletResponse response)
			throws IOException {
		getLostVelocityEngine().merge(template, context, response.getWriter());
	}

	/**
	 * Retrieve the Velocity template specified by the given name, using the
	 * encoding specified by the "encoding" bean property.
	 * <p>
	 * Can be called by subclasses to retrieve a specific template, for example
	 * to render multiple templates into a single view.
	 * 
	 * @param name
	 *            the file name of the desired template
	 * @return the Velocity template
	 * @throws Exception
	 *             if thrown by Velocity
	 * @see org.apache.velocity.app.VelocityEngine#getTemplate
	 */
	protected Template getTemplate(String name) throws Exception {
		return (getEncoding() != null ? getLostVelocityEngine().getVelocityEngine().getTemplate(
				name, getEncoding()) : getLostVelocityEngine().getVelocityEngine()
																.getTemplate(name));
	}

	/**
	 * Return the encoding for the Velocity template.
	 */
	protected String getEncoding() {
		return this.encoding;
	}

	/**
	 * Check that the Velocity template used for this view exists and is valid.
	 * <p>
	 * Can be overridden to customize the behavior, for example in case of
	 * multiple templates to be rendered into a single view.
	 */
	@Override
	public boolean checkResource(Locale locale) throws Exception {

		// Check that we can get the template, even if we might subsequently
		// get it again.
		if (getLostVelocityEngine().getVelocityEngine().resourceExists(getUrl())) {
			this.template = getTemplate(getUrl());
			return true;
		}
		return false;

	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	@Override
	protected Map<String, Object> createMergedOutputModel(Map<String, ?> model,
			HttpServletRequest request, HttpServletResponse response) {
		return super.createMergedOutputModel(model, request, response);
	}
}
