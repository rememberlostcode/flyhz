
package com.flyhz.shop.persistence.dao;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.shop.persistence.entity.UserModel;

public interface UserDao extends GenericDao<UserModel> {

	public void register(UserModel userModel) throws ValidateException;

	public UserModel getUserByName(String username) throws ValidateException;

}
