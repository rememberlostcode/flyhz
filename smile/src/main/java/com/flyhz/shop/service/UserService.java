
package com.flyhz.shop.service;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.shop.dto.Consignee;
import com.flyhz.shop.dto.User;
import com.flyhz.shop.dto.UserDetail;

public interface UserService {
	/**
	 * 用户注册
	 * 
	 * @param userDetail
	 * @return User
	 */
	public User register(UserDetail userDetail);

	public User login(String username, String password, String verifycode) throws ValidateException;

	public void logout(Integer userId);

	public Consignee getConsignee(Integer userId, Integer consigneeId);

	public Consignee addConsignee(Consignee consignee);

	public Consignee modifyConsignee(Consignee consignee);

	public void removeConsignee(Integer userId, Integer consigneeId);

	public void listConsignees(Integer userId);

	public void setPersonalInformation(Integer userId, String field, Object value);

	public UserDetail getPersonalInformation(Integer userId);

	public void resetpwd(Integer userId, String oldpwd, String newpwd);

}