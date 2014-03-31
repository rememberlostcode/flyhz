
package com.flyhz.framework.view.velocity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.io.VelocityWriter;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.tools.ToolManager;
import org.apache.velocity.tools.ToolboxFactory;
import org.apache.velocity.tools.config.ConfigurationCleaner;
import org.apache.velocity.tools.config.ConfigurationUtils;
import org.apache.velocity.tools.config.FactoryConfiguration;
import org.apache.velocity.tools.view.JeeConfig;
import org.apache.velocity.tools.view.ServletUtils;
import org.apache.velocity.tools.view.ViewToolContext;
import org.apache.velocity.util.SimplePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.ui.velocity.CommonsLoggingLogSystem;
import org.springframework.ui.velocity.SpringResourceLoader;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.flyhz.framework.config.FinderConfig;
import com.flyhz.framework.repository.file.FileUtil;

public class LostVelocityEngine extends ToolManager {

	protected Logger			log								= LoggerFactory.getLogger(getClass());

	/** The HTTP content type context key. */
	public static final String	CONTENT_TYPE_KEY				= "default.contentType";
	/** The default content type for the response */
	public static final String	DEFAULT_CONTENT_TYPE			= "text/html";
	/** Default encoding for the output stream */
	public static final String	DEFAULT_OUTPUT_ENCODING			= "ISO-8859-1";
	/**
	 * Default toolbox configuration file path. If no alternate value for this
	 * is specified, the servlet will look here.
	 */
	public static final String	USER_TOOLS_PATH					= "/WEB-INF/tools.xml";

	/**
	 * Controls loading of available default tool configurations provided by
	 * VelocityTools. The default behavior is conditional; if
	 * {@link #DEPRECATION_SUPPORT_MODE_KEY} has not been set to {@code false}
	 * and there is an old {@code toolbox.xml} configuration present, then the
	 * defaults will not be loaded unless you explicitly set this property to
	 * {@code true} in your init params. If there is no {@code toolbox.xml}
	 * and/or the deprecation support is turned off, then the default tools will
	 * be loaded automatically unless you explicitly set this property to
	 * {@code false} in your init params.
	 */
	public static final String	LOAD_DEFAULTS_KEY				= "org.apache.velocity.tools.loadDefaults";

	/**
	 * Controls removal of tools or data with invalid configurations before
	 * initialization is finished. The default is false; set to {@code true} to
	 * turn this feature on.
	 */
	public static final String	CLEAN_CONFIGURATION_KEY			= "org.apache.velocity.tools.cleanConfiguration";

	/**
	 * Controls whether or not templates can overwrite tool and servlet API
	 * variables in the local context. The default is true; set to {@code false}
	 * to prevent overwriting of any tool variables.
	 */
	public static final String	USER_OVERWRITE_KEY				= "org.apache.velocity.tools.userCanOverwriteTools";
	/**
	 * Controls support for deprecated tools and configuration. The default is
	 * {@code true}; set to {@code false} to turn off support for deprecated
	 * tools and configuration.
	 */
	public static final String	DEPRECATION_SUPPORT_MODE_KEY	= "org.apache.velocity.tools.deprecationSupportMode";

	private static SimplePool	writerPool						= new SimplePool(40);
	private String				defaultContentType				= DEFAULT_CONTENT_TYPE;

	private FinderConfig			config;

	public LostVelocityEngine(FinderConfig config) throws VelocityException, IOException {
		this.config = config;
		newVelocityEngine();
		newToolboxFactory();
		// set encoding & content-type
		setEncoding();

	}

	protected void setEncoding() {
		// we can get these now that velocity is initialized
		this.defaultContentType = getProperty(CONTENT_TYPE_KEY, DEFAULT_CONTENT_TYPE);

		String encoding = getProperty(RuntimeConstants.OUTPUT_ENCODING, DEFAULT_OUTPUT_ENCODING);

		// For non Latin-1 encodings, ensure that the charset is
		// included in the Content-Type header.
		if (!DEFAULT_OUTPUT_ENCODING.equalsIgnoreCase(encoding)) {
			int index = defaultContentType.lastIndexOf("charset");
			if (index < 0) {
				// the charset specifier is not yet present in header.
				// append character encoding to default content-type
				this.defaultContentType += "; charset=" + encoding;
			} else {
				// The user may have configuration issues.
				log.info("Charset was already specified in the Content-Type property.Output encoding property will be ignored.");
			}
		}

		log.debug("Default Content-Type is: {}", defaultContentType);
	}

	/**
	 * Here's the configuration lookup/loading order:
	 * <ol>
	 * <li>If deprecationSupportMode is true:
	 * <ol>
	 * <li>Config file optionally specified by
	 * {@code org.apache.velocity.toolbox} init-param (servlet or
	 * servletContext)</li>
	 * <li>If none, config file optionally at {@code /WEB-INF/toolbox.xml}
	 * (deprecated conventional location)</li>
	 * </ol>
	 * </li>
	 * <li>If no old toolbox or loadDefaults is true,
	 * {@link ConfigurationUtils#getDefaultTools()}</li>
	 * <li>{@link ConfigurationUtils#getAutoLoaded}(false)</li>
	 * <li>Config file optionally specified by servletContext
	 * {@code org.apache.velocity.tools} init-param</li>
	 * <li>Config file optionally at {@code /WEB-INF/tools.xml} (new
	 * conventional location)</li>
	 * <li>Config file optionally specified by servlet
	 * {@code org.apache.velocity.tools} init-param</li>
	 * </ol>
	 * Remember that as these configurations are added on top of each other, the
	 * newer values will always override the older ones. Also, once they are all
	 * loaded, this method can "clean" your configuration of all invalid tool,
	 * toolbox or data configurations if you set the
	 * {@code org.apache.velocity.tools.cleanConfiguration} init-param to true
	 * in either your servlet or servletContext init-params.
	 */
	protected void configureToolboxFactory() {
		FactoryConfiguration factoryConfig = new FactoryConfiguration(
				"VelocityView.configure(config,factory)");

		String loadDefaults = (String) this.velocity.getProperty(LOAD_DEFAULTS_KEY);
		if ((loadDefaults == null) || "true".equalsIgnoreCase(loadDefaults)) {
			log.trace("Loading velocityView tools configuration...");
			ResourceLoader resourceLoader = (ResourceLoader) this.config.getConfig(FinderConfig.SPRING_RESOURCE_LOADER);
			Resource reousrce = resourceLoader.getResource("classpath:velocity/tools.xml");
			try {
				FactoryConfiguration config = ConfigurationUtils.read(reousrce.getURL());
				ConfigurationUtils.clean(config);
				factoryConfig.addConfiguration(config);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			log.debug("Default tools configuration has been suppressed.");
		}

		// this gets the auto loaded config from the classpath
		// this doesn't include defaults since they're handled already
		// and it could theoretically pick up an auto-loaded config from the
		// filesystem, but that is highly unlikely to happen in a webapp env
		FactoryConfiguration autoLoaded = ConfigurationUtils.getAutoLoaded(false);
		factoryConfig.addConfiguration(autoLoaded);

		// check for user configuration at the conventional location,
		// and be silent if they're missing
		setConfig(factoryConfig, USER_TOOLS_PATH, false);

		// see if we should only keep valid tools, data, and properties
		String cleanConfig = (String) this.velocity.getProperty(CLEAN_CONFIGURATION_KEY);
		if ("true".equals(cleanConfig)) {
			// remove invalid tools, data, and properties from the configuration
			ConfigurationCleaner cleaner = new ConfigurationCleaner();
			cleaner.setLog(this.velocity.getLog());
			cleaner.clean(factoryConfig);
		}

		String userCanOverwrite = (String) this.velocity.getProperty(USER_OVERWRITE_KEY);
		if ("false".equals(userCanOverwrite)) {
			setUserCanOverwriteTools(false);
		}

		// apply this configuration to the specified factory
		log.debug("Configuring factory with: {}", factoryConfig);
		this.factory.configure(factoryConfig);
	}

	/**
	 * Return a new VelocityEngine. Subclasses can override this for custom
	 * initialization, or for using a mock object for testing.
	 * <p>
	 * Called by <code>createVelocityEngine()</code>.
	 * 
	 * @return the VelocityEngine instance
	 * @throws IOException
	 *             if a config file wasn't found
	 * @throws VelocityException
	 *             on Velocity initialization failure
	 * @see #createVelocityEngine()
	 */
	protected void newVelocityEngine() throws IOException, VelocityException {

		this.velocity = new VelocityEngine();

		Map<String, Object> props = new HashMap<String, Object>();

		// Load config file if set.
		Resource velocityPropertiesResource = (Resource) config.getConfig(FinderConfig.VELOCITY_PROPERTIES_RESOURCE);
		if (velocityPropertiesResource != null) {

			log.info("Loading Velocity config from [{}]", velocityPropertiesResource);

			CollectionUtils.mergePropertiesIntoMap(
					PropertiesLoaderUtils.loadProperties(velocityPropertiesResource), props);
		}

		// Set a resource loader path, if required.
		// String velocityVmPath = (String)
		// config.getConfig(FinderConfig.VELOCITY_VM_ROOT_PATH);

		if (config.getConfig(FinderConfig.WEB_VM_SCREEN_ROOT_PATH) == null) {
			throw new RuntimeException(FinderConfig.WEB_VM_SCREEN_ROOT_PATH + " is null");
		}
		String velocityScreenVmPath = (String) config.getConfig(FinderConfig.WEB_VM_SCREEN_ROOT_PATH);
		if (config.getConfig(FinderConfig.WEB_VM_LAYOUT_ROOT_PATH) == null) {
			throw new RuntimeException(FinderConfig.WEB_VM_LAYOUT_ROOT_PATH + " is null");
		}

		String velocityLayoutVmPath = (String) config.getConfig(FinderConfig.WEB_VM_LAYOUT_ROOT_PATH);
		StringBuffer velocityVmPath = new StringBuffer(30);
		velocityVmPath.append(velocityLayoutVmPath).append(",").append(velocityScreenVmPath);
		initVelocityResourceLoader(getVelocityEngine(), velocityVmPath.toString());

		// Log via Commons Logging?
		if (Boolean.TRUE.equals(config.getConfig(FinderConfig.LOST_VELOCITY_OVERRIDELOGGING))) {
			velocity.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM,
					new CommonsLoggingLogSystem());
		}

		// Apply properties to VelocityEngine.
		for (Map.Entry<String, Object> entry : props.entrySet()) {
			velocity.setProperty(entry.getKey(), entry.getValue());
		}

		postProcessVelocityEngine(velocity);

		try {
			// Perform actual initialization.
			velocity.init();
		} catch (VelocityException ex) {
			throw ex;
		} catch (RuntimeException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Why does LostVelocityEngine throw a generic checked exception, after all?",
					ex);
			throw new VelocityException(ex.toString());
		}

	}

	protected void newToolboxFactory() {
		this.factory = new ToolboxFactory();
		configureToolboxFactory();
	}

	/**
	 * Initialize a Velocity resource loader for the given VelocityEngine:
	 * either a standard Velocity FileResourceLoader or a SpringResourceLoader.
	 * <p>
	 * Called by <code>createVelocityEngine()</code>.
	 * 
	 * @param velocityEngine
	 *            the VelocityEngine to configure
	 * @param resourceLoaderPath
	 *            the path to load Velocity resources from
	 * @throws IOException
	 * @see org.apache.velocity.runtime.resource.loader.FileResourceLoader
	 * @see SpringResourceLoader
	 * @see #initSpringResourceLoader
	 * @see #createVelocityEngine()
	 */
	protected void initVelocityResourceLoader(VelocityEngine velocityEngine,
			String resourceLoaderPath) throws IOException {
		StringBuilder resolvedPath = new StringBuilder();
		String[] paths = StringUtils.commaDelimitedListToStringArray(resourceLoaderPath);
		ResourceLoader resourceLoader = (ResourceLoader) this.config.getConfig(FinderConfig.SPRING_RESOURCE_LOADER);
		for (int i = 0; i < paths.length; i++) {
			String path = paths[i];
			// .append(LostVelocityLayoutView.DEFAULT_LAYOUT_DO_NOT_USE).append(",")

			Resource resource = resourceLoader.getResource(path);

			File file = resource.getFile(); // will fail if not resolvable
											// in the file system
			if (file.getAbsolutePath().indexOf("layout") > 0) {
				File defaultLayout = new File(file.getAbsolutePath() + File.separator
						+ LostVelocityLayoutView.DEFAULT_LAYOUT);
				if (defaultLayout.exists()) {
					defaultLayout.delete();
				}
				defaultLayout.createNewFile();

				OutputStream os = new FileOutputStream(defaultLayout);
				InputStream is = resourceLoader.getResource(
						"classpath:velocity/" + LostVelocityLayoutView.DEFAULT_LAYOUT).getURL()
												.openStream();
				FileUtil.copy(is, os);

				File doNotUseLayout = new File(file.getAbsolutePath() + File.separator
						+ LostVelocityLayoutView.DEFAULT_LAYOUT_DO_NOT_USE);
				if (doNotUseLayout.exists()) {
					doNotUseLayout.delete();
				}
				doNotUseLayout.createNewFile();
				FileUtil.copy(
						resourceLoader.getResource(
								"classpath:velocity/"
										+ LostVelocityLayoutView.DEFAULT_LAYOUT_DO_NOT_USE)
										.getURL().openStream(),
						new FileOutputStream(doNotUseLayout));
			}

			log.debug("Resource loader path [{}] resolved to file [{}]", path,
					file.getAbsolutePath());

			resolvedPath.append(file.getAbsolutePath());
			if (i < paths.length - 1) {
				resolvedPath.append(',');
			}
		}
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
		velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE, "true");
		velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH,
				resolvedPath.toString());
		initSpringResourceLoader(velocityEngine, resourceLoaderPath, resourceLoader);
	}

	/**
	 * Simplifies process of getting a property from VelocityEngine, because the
	 * VelocityEngine interface sucks compared to the singleton's. Use of this
	 * method assumes that {@link #init(JeeConfig,VelocityEngine)} has already
	 * been called.
	 */
	protected String getProperty(String key, String alternate) {
		String prop = (String) velocity.getProperty(key);
		if (prop == null || prop.length() == 0) {
			return alternate;
		}
		return prop;
	}

	private boolean setConfig(FactoryConfiguration factory, String path, boolean require) {
		if (path == null) {
			// only bother with this if a path was given
			return false;
		}

		// this will throw an exception if require is true and there
		// is no tool config at the path. if require is false, this
		// will return null when there's no tool config at the path
		FactoryConfiguration config = getConfiguration(path, require);
		if (config == null) {
			return false;
		}

		log.debug("Loaded configuration from: {}", path);
		factory.addConfiguration(config);

		// notify that new config was added
		return true;
	}

	protected FactoryConfiguration getConfiguration(String path, boolean required) {

		log.trace("Searching for configuration at: {}", path);

		FactoryConfiguration config = null;
		try {
			// 仅支持新的tool.xml
			config = ServletUtils.getConfiguration(path,
					(ServletContext) this.config.getConfig(FinderConfig.SPRING_MVC_SERVLET), false);
			if (config == null) {
				String msg = "Did not find resource at: " + path;
				if (required) {
					log.error(msg);
					throw new ResourceNotFoundException(msg);
				} else {
					log.debug(msg);
				}
			}
		} catch (ResourceNotFoundException rnfe) {
			// no need to re-LOG this
			throw rnfe;
		} catch (RuntimeException re) {
			if (required) {
				log.error(re.getMessage(), re);
				throw re;
			}
			log.debug(re.getMessage(), re);
		}
		return config;
	}

	/**
	 * Merges the template with the context. Only override this if you really,
	 * really really need to. (And don't call us with questions if it breaks :)
	 * 
	 * @param template
	 *            template being rendered
	 * @param context
	 *            Context created by the {@link #createContext}
	 * @param writer
	 *            into which the content is rendered
	 */
	public void merge(Template template, Context context, Writer writer) throws IOException {
		VelocityWriter vw = null;
		try {
			vw = (VelocityWriter) writerPool.get();
			if (vw == null) {
				vw = new VelocityWriter(writer, 4 * 1024, true);
			} else {
				vw.recycle(writer);
			}
			performMerge(template, context, vw);

			// flush writer but don't close to allow us to play nicely with
			// others.
			vw.flush();
		} finally {
			if (vw != null) {
				try {
					/*
					 * This hack sets the VelocityWriter's internal ref to the
					 * PrintWriter to null to keep memory free while the writer
					 * is pooled. See bug report #18951
					 */
					vw.recycle(null);
					writerPool.put(vw);
				} catch (Exception e) {
					log.error("Trouble releasing VelocityWriter: " + e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * This is here so developers may override it and gain access to the Writer
	 * which the template will be merged into. See <a
	 * href="http://issues.apache.org/jira/browse/VELTOOLS-7">VELTOOLS-7</a> for
	 * discussion of this.
	 * 
	 * @param template
	 *            template object returned by the handleRequest() method
	 * @param context
	 *            Context created by the {@link #createContext}
	 * @param writer
	 *            a VelocityWriter that the template is merged into
	 */
	protected void performMerge(Template template, Context context, Writer writer)
			throws IOException {
		template.merge(context, writer);
	}

	/**
	 * To be implemented by subclasses that want to to perform custom
	 * post-processing of the VelocityEngine after this FactoryBean performed
	 * its default configuration (but before VelocityEngine.init).
	 * <p>
	 * Called by <code>createVelocityEngine()</code>.
	 * 
	 * @param velocityEngine
	 *            the current VelocityEngine
	 * @throws IOException
	 *             if a config file wasn't found
	 * @throws VelocityException
	 *             on Velocity initialization failure
	 * @see #createVelocityEngine()
	 * @see org.apache.velocity.app.VelocityEngine#init
	 */
	protected void postProcessVelocityEngine(VelocityEngine velocityEngine) throws IOException,
			VelocityException {
		velocityEngine.setApplicationAttribute(ServletContext.class.getName(),
				this.config.getConfig(FinderConfig.SPRING_MVC_SERVLET));
		velocityEngine.setProperty(LOST_MACRO_RESOURCE_LOADER_CLASS,
				ClasspathResourceLoader.class.getName());
		velocityEngine.addProperty(VelocityEngine.RESOURCE_LOADER, LOST_MACRO_RESOURCE_LOADER_NAME);
		velocityEngine.setProperty(VelocityEngine.VM_LIBRARY,
				"velocity/macro.vm,velocity/macro_default.vm");

	}

	/** the key for the class of Lost's bind macro resource loader */
	private static final String	LOST_MACRO_RESOURCE_LOADER_CLASS	= "lostMacro.resource.loader.class";

	/** the name of the resource loader for Spring's bind macros */
	private static final String	LOST_MACRO_RESOURCE_LOADER_NAME		= "lostMacro";

	public Context createContext(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) {
		ViewToolContext ctx = new ViewToolContext(velocity, request, response,
				(ServletContext) config.getConfig(FinderConfig.SPRING_MVC_SERVLET));
		ctx.putAll(model);
		prepareContext(ctx);
		return ctx;
	}

	/**
	 * Initialize a SpringResourceLoader for the given VelocityEngine.
	 * <p>
	 * Called by <code>initVelocityResourceLoader</code>.
	 * 
	 * @param velocityEngine
	 *            the VelocityEngine to configure
	 * @param resourceLoaderPath
	 *            the path to load Velocity resources from
	 * @see SpringResourceLoader
	 * @see #initVelocityResourceLoader
	 */
	protected void initSpringResourceLoader(VelocityEngine velocityEngine,
			String resourceLoaderPath, ResourceLoader resourceLoader) {
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, SpringResourceLoader.NAME);
		velocityEngine.setProperty(SpringResourceLoader.SPRING_RESOURCE_LOADER_CLASS,
				SpringResourceLoader.class.getName());
		// 缓存vm
		velocityEngine.setProperty(SpringResourceLoader.SPRING_RESOURCE_LOADER_CACHE, "false");
		velocityEngine.setApplicationAttribute(SpringResourceLoader.SPRING_RESOURCE_LOADER,
				resourceLoader);
		velocityEngine.setApplicationAttribute(SpringResourceLoader.SPRING_RESOURCE_LOADER_PATH,
				resourceLoaderPath);
	}
}
