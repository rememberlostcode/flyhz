
package com.flyhz.framework.util;

import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyResourceConfigurer;

/**
 * 
 * 类说明:异常抛出code工具类，获取系统参数和XX.properties中的参数
 * 
 * @author robin
 * @version 创建时间：2013-6-3 下午3:13:00
 * 
 */
public class ExceptionUtil extends PropertyResourceConfigurer {

	private static final Logger	logger	= Logger.getLogger(ExceptionUtil.class);
	private static Properties	properties;

	public static String getConfig(String key) {
		if (StringUtils.isBlank(key))
			return null;
		try {
			String value = properties.getProperty(key);
			value = new String(value.getBytes("ISO-8859-1"), "UTF-8");
			return value;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 取得多个key对应的值
	 * 
	 * @param keys
	 *            key字符串，用","号分隔
	 * @return
	 */
	public static String getConfigByKeys(String keys) {
		if (StringUtils.isBlank(keys))
			return null;
		String value = "";
		try {
			String[] arrKey = keys.split(",");
			String tempKey = "";
			for (String key : arrKey) {
				if (key.equals(tempKey))
					continue;
				tempKey = key;
				value += properties.getProperty(key) + ";";
			}
			value = new String(value.getBytes("ISO-8859-1"), "UTF-8");
			return value;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getConfig(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	@SuppressWarnings("rawtypes")
	public static void appendProperties(Properties paramProperties) {
		if (properties == null) {
			properties = paramProperties;
		} else {
			Enumeration enums = paramProperties.propertyNames();
			while (enums.hasMoreElements()) {
				String key = (String) enums.nextElement();
				properties.setProperty(key, paramProperties.getProperty(key));
				logger.info("add config:  " + key + " = '" + paramProperties.getProperty(key));
			}
		}
	}

	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactory,
			Properties paramProps) throws BeansException {
		appendProperties(paramProps);
	}
}
