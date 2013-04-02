
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

	/**
	 * 添加收件人地址
	 * 
	 * @param consignee
	 * @return ConsigneeModel
	 */
	public ConsigneeModel addConsignee(ConsigneeModel consignee) throws ValidateException;

	/**
	 * 修改收件人地址
	 * 
	 * @param consignee
	 * @return ConsigneeModel
	 */
	public ConsigneeModel modifyConsignee(ConsigneeModel consignee) throws ValidateException;

	/**
	 * 删除收件人地址
	 * 
	 * @param userId
	 * @param consigneeId
	 * @return
	 */
	public void removeConsignee(Integer userId, Integer consigneeId) throws ValidateException;

	public void listConsignees(Integer userId);

	/**
	 * 设置个人信息
	 * 
	 * @param userId
	 * @param field
	 * @param value
	 * @return
	 */
	public void setPersonalInformation(Integer userId, String field, Object value)
			throws ValidateException;

	public UserDetailDto getPersonalInformation(Integer userId);

	public void resetpwd(Integer userId, String oldpwd, String newpwd);

}