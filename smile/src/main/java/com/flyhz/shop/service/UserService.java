
package com.flyhz.shop.service;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.shop.dto.UserDetailDto;
import com.flyhz.shop.dto.UserDto;
import com.flyhz.shop.persistence.entity.ConsigneeModel;

public interface UserService {
	/**
	 * 用户注册
	 * 
	 * @param userDetail
	 * @return User
	 */
	public UserDto register(UserDetailDto userDetail) throws ValidateException;

	/**
	 * 用户名密码登录
	 * 
	 * @param username
	 *            用户名
	 * @param password
	 *            用户密码，MD5加密
	 * @param verifycode
	 *            保留
	 * @return
	 * @throws ValidateException
	 */
	public UserDto login(String username, String password, String verifycode)
			throws ValidateException;

	/**
	 * 自动登录
	 * 
	 * @param userId
	 *            用户id
	 * @param token
	 *            用户token
	 * @param verifycode
	 *            保留
	 * @return
	 * @throws ValidateException
	 */
	public UserDto loginAuto(Integer userId, String token, String verifycode)
			throws ValidateException;

	/**
	 * 注销
	 * 
	 * @param userId
	 *            用户id
	 */
	public void logout(Integer userId) throws ValidateException;

	public ConsigneeModel getConsignee(Integer userId, Integer consigneeId);

	public ConsigneeModel addConsignee(ConsigneeModel consignee);

	public ConsigneeModel modifyConsignee(ConsigneeModel consignee);

	public void removeConsignee(Integer userId, Integer consigneeId);

	public void listConsignees(Integer userId);

	public void setPersonalInformation(Integer userId, String field, Object value);

	public UserDetailDto getPersonalInformation(Integer userId);

	public void resetpwd(Integer userId, String oldpwd, String newpwd);

}