
package com.flyhz.shop.persistence.dao;

import java.util.List;
import java.util.Map;

/**
 * @author fwb 2013-05-06 通用DAO接口
 */
public interface GenericDao<T> {
	/**
	 * 增加记录
	 * 
	 * @param t
	 * @return
	 */
	public int insert(T t);

	/**
	 * 修改记录
	 * 
	 * @param t
	 * @return
	 */
	public int update(T t);

	/**
	 * 删除记录
	 * 
	 * @param t
	 * @return
	 */
	public int delete(T t);

	/**
	 * 查询单条数据
	 * 
	 * @param id
	 * @return
	 */
	public T getModelById(Integer id);

	/**
	 * 查询单条数据
	 * 
	 * @param t
	 * @return
	 */
	public T getModel(T t);

	/**
	 * 批量插入
	 * 
	 * @param list
	 * @return
	 */
	public int batchInsert(List<?> list);

	/**
	 * 批量删除
	 * 
	 * @param map
	 * @return
	 */
	public int batchDelete(Map<String, Object> map);

	/**
	 * 批量删除
	 * 
	 * @param list
	 * @return
	 */
	public int batchDelete(List<?> list);

	/**
	 * 批量修改
	 * 
	 * @param map
	 * @return
	 */
	public int batchUpdate(Map<String, Object> map);

	/**
	 * 分页查询
	 * 
	 * @param t
	 * @return
	 */
	public List<T> getPagedList(T t);

	/**
	 * 无分页查询
	 * 
	 * @param t
	 * @return
	 */
	public List<T> getModelList(T t);

	/**
	 * 无分页查询
	 * 
	 * @param str
	 * @return
	 */
	public List<T> getModelList(String... str);

	/**
	 * 统计记录数
	 * 
	 * @param t
	 * @return
	 */
	public int getCount(T t);

	/**
	 * 按数据范围分页查询
	 * 
	 * @param t
	 * @return
	 */
	public List<T> getPagedByScope(T t);

	/**
	 * 按数据范围无分页查询
	 * 
	 * @param t
	 * @return
	 */
	public List<T> getModelListByScope(T t);
}
