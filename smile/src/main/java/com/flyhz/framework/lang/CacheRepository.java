
package com.flyhz.framework.lang;

import java.util.List;
import java.util.Map;

public interface CacheRepository {

	public void delete(String key);

	/**
	 * 获取指定key的值
	 * 
	 * @param key
	 * @return
	 */
	public String getString(String key);

	/**
	 * 设置指定key的值
	 * 
	 * @param key
	 * @param value
	 */
	public void setString(String key, String value);

	/**
	 * 向名称为key的hash中添加元素field<—>value
	 * 
	 * @param key
	 * @param field
	 * @param value
	 */
	public void hset(String key, String field, String value);

	/**
	 * 返回名称为key的hash中field对应的value
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public String hget(String key, String field);

	/**
	 * 删除名称为key的hash中键为field的域
	 * 
	 * @param key
	 * @param field
	 */
	public void hdel(String key, String field);

	/**
	 * 返回名称为key的hash中所有的键（field）及其对应的value
	 * 
	 * @param key
	 * @return
	 */
	public Map<String, String> hgetall(String key);

	/**
	 * 在名称为key的list尾添加一个值为value的元素
	 * 
	 * @param key
	 * @param value
	 */
	public void rpush(String key, String value);

	/**
	 * 在名称为key的list头添加一个值为value的 元素
	 * 
	 * @param key
	 * @param value
	 */
	public void lpush(String key, String value);

	/**
	 * 返回名称为key的list的长度
	 * 
	 * @param key
	 * @return
	 */
	public Long llen(String key);

	/**
	 * 删除名称为key的list中值为value的元素
	 * 
	 * @param key
	 * @param value
	 */
	public void lrem(String key, String value);

	/**
	 * 返回名称为key的list中的所有元素
	 * 
	 * @param key
	 * @return
	 */
	public List<String> lrange(String key);
}