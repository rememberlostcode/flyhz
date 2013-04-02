
package com.flyhz.shop.persistence.dao;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.shop.persistence.entity.UserModel;

public interface UserDao extends GenericDao<UserModel> {

	public void register(UserModel userModel) throws ValidateException;

	public UserModel getUserByName(String username) throws ValidateException;

	/**
	 * 更新用户邮箱
	 * 
	 * @author fuwb 20140401
	 * @param userModel
	 * @return
	 */
	public int updateEmail(UserModel userModel);

	/**
	 * 更新用户电话
	 * 
	 * @author fuwb 20140401
	 * @param userModel
	 * @return
	 */
	public int updateMobilePhone(UserModel userModel);

	/**
	 * 更新用户密码
	 * 
	 * @author fuwb 20140401
	 * @param userModel
	 * @return
	 */
	public int updatePwd(UserModel userModel);
}
