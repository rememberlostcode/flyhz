
package com.flyhz.framework.lang;

import java.util.Map;

public interface CacheRepository {

	public String getString(String key, Class<?> clazz);

	public Object getObject(String key, Class<?> clazz);

	public void set(String key, Object value);

	public void set(String prefix, String key, String value);

	public String getString(String prefix, String key);

	public void set(Map<String, Object> map);

	public void setNull(String key, Object value);

	/**
	 * 设置string字符串
	 * 
	 * @param prefix
	 *            前缀
	 * @param key
	 * @param value
	 */
	public void setString(String prefix, String key, String value);

	/**
	 * 设置string字符串为空
	 * 
	 * @param prefix
	 * @param key
	 */
	public void setStringNull(String prefix, String key);
}