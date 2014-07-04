
package com.flyhz.shop.service;

import org.springframework.web.multipart.MultipartFile;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.shop.persistence.entity.VersionModel;

/**
 * 版本service
 * 
 * @author zhangb
 * 
 */
public interface VersionService {

	/**
	 * 插入新版本
	 * 
	 * @param versionModel
	 * @throws ValidateException
	 */
	public void insertVersion(VersionModel versionModel,MultipartFile file) throws ValidateException;

	/**
	 * 获取最新的版本
	 * 
	 * @return
	 */
	public VersionModel getLastestModel();
}
