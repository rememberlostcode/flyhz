
package com.flyhz.shop.persistence.dao;

import com.flyhz.shop.persistence.entity.VersionModel;

/**
 * 版本Dao
 * 
 * @author zhangb 2014-07-03 23:29:24
 * 
 */
public interface VersionDao extends GenericDao<VersionModel> {

	/**
	 * 获得最新的版本信息
	 * 
	 * @return
	 */
	public VersionModel getLastestModel();

}
