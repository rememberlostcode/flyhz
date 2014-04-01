
package com.flyhz.framework;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.context.ServletContextAware;

import com.flyhz.framework.lang.velocity.LostVelocityEngine;
import com.flyhz.framework.lang.velocity.LostVelocityLayoutViewResolver;

/**
 * LostWebCore所有的属性配置均由该类接管，包括velocity.properties的路径配置，spring
 * resolver的前缀，后缀，资源路径,velocity的配置等
 * 
 * @author huoding
 * 
 */

public class FinderConfig implements ServletContextAware, ResourceLoaderAware, InitializingBean {

	private Map<String, Object>	properties										= new HashMap<String, Object>();

	/**
	 * springmvc servlet
	 */

	public static final String	SPRING_MVC_SERVLET								= "org.lost.finder.spring.mvc.servlet";

	/**
	 * {@link LostVelocityLayoutViewResolver} view后缀名
	 */

	private static final String	DEFAULT_SPRING_URL_BASED_VIEW_RESOLVER_SUFFIX	= "org.lost.finder.spring.resolver.suffix";

	private static final String	DEFAULT_SPRING_RESOURCE_LOADER					= "org.lost.finder.spring.resource.loader";

	// 需要配置成classpath形式,默认classpath:veloctiy.properties
	private static final String	DEFAULT_VELOCITY_PROPERTIES_RESOURCE			= "org.lost.finder.velocity.properties.resource";

	/**
	 * {@link LostVelocityEngine}
	 */
	private static final String	DEFAULT_LOST_VELOCITY							= "org.lost.finder.velocity";

	/**
	 * 是否覆盖velocity日志true/false
	 */
	private static final String	DEFAULT_LOST_VELOCITY_OVERRIDELOGGING			= "org.lost.finder.velocity.overrideLogging";

	/**
	 * layout vm存放路径
	 */
	private static final String	DEFAULT_WEB_VM_LAYOUT_ROOT_PATH					= "org.lost.finder.vm.layout.root.path";
	/**
	 * screen vm存放路径
	 */
	private static final String	DEFAULT_WEB_VM_SCREEN_ROOT_PATH					= "org.lost.finder.vm.screen.root.path";

	private static final String	DEFAULT_WEB_ENCODING							= "org.lost.finder.encoding";
	private static final String	DEFAULT_WEB_CONTENT_TYPE						= "org.lost.finder.content.type";

	private static final String	DEFAULT_FILE_UPLOAD_PATH						= "org.lost.finder.fileupload.path";
	private static final String	DEFAULT_FILE_UPLOAD_TEMP_PATH					= "org.lost.finder.fileupload.temp.path";
	private static final String	DEFAULT_FILE_SIZE_THRESHOLD						= "org.lost.finder.file.sizeThreshold";
	private static final String	DEFAULT_FILE_STORE_TEMP_PATH					= "org.lost.finder.filestore.temp.path";

	private static String		DEFAULT_FILE_STATUS_CLEAN_PERIOD				= "org.lost.fileupload.status.clean.period";
	private static String		DEFAULT_FILE_STATUS_CLEAN_DELAY					= "org.lost.fileupload.status.clean.delay";

	public static String		SPRING_URL_BASED_VIEW_RESOLVER_SUFFIX			= DEFAULT_SPRING_URL_BASED_VIEW_RESOLVER_SUFFIX;
	public static String		SPRING_RESOURCE_LOADER							= DEFAULT_SPRING_RESOURCE_LOADER;
	public static String		VELOCITY_PROPERTIES_RESOURCE					= DEFAULT_VELOCITY_PROPERTIES_RESOURCE;
	public static String		LOST_VELOCITY									= DEFAULT_LOST_VELOCITY;

	public static String		LOST_VELOCITY_OVERRIDELOGGING					= DEFAULT_LOST_VELOCITY_OVERRIDELOGGING;
	/**
	 * layout vm存放路径
	 */
	public static String		WEB_VM_LAYOUT_ROOT_PATH							= DEFAULT_WEB_VM_LAYOUT_ROOT_PATH;
	/**
	 * screen vm存放路径
	 */
	public static String		WEB_VM_SCREEN_ROOT_PATH							= DEFAULT_WEB_VM_SCREEN_ROOT_PATH;

	public static String		WEB_ENCODING									= DEFAULT_WEB_ENCODING;
	public static String		WEB_CONTENT_TYPE								= DEFAULT_WEB_CONTENT_TYPE;

	public static String		FILE_UPLOAD_PATH								= DEFAULT_FILE_UPLOAD_PATH;
	public static String		FILE_UPLOAD_TEMP_PATH							= DEFAULT_FILE_UPLOAD_TEMP_PATH;
	public static String		FILE_SIZE_THRESHOLD								= DEFAULT_FILE_SIZE_THRESHOLD;
	public static String		FILE_STORE_TEMP_PATH							= DEFAULT_FILE_STORE_TEMP_PATH;

	//
	public static String		FILE_STATUS_CLEAN_PERIOD						= DEFAULT_FILE_STATUS_CLEAN_PERIOD;
	public static String		FILE_STATUS_CLEAN_DELAY							= DEFAULT_FILE_STATUS_CLEAN_DELAY;

	public Object getConfig(String key) {
		return this.properties.get(key);
	}

	public void setProperties(Map<String, Object> properties) {
		for (Entry<String, Object> entry : properties.entrySet()) {
			if (entry.getValue() instanceof String) {
				String value = (String) entry.getValue();
				if (value.startsWith("${") && value.endsWith("}")) {
					this.properties.put(entry.getKey(), null);
				}
			}
			this.properties.put(entry.getKey(), entry.getValue());
		}

	}

	public void setProperties(String key, Object value) {
		this.properties.put(key, value);
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		properties.put(SPRING_MVC_SERVLET, servletContext);
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.properties.put(SPRING_RESOURCE_LOADER, resourceLoader);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.properties.put(LOST_VELOCITY, new LostVelocityEngine(this));
	}
}
