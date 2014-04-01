
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
	public UserDto register(UserDetailDto userDetail);

	public UserDto login(String username, String password, String verifycode)
			throws ValidateException;

	public void logout(Integer userId);

	public ConsigneeModel getConsignee(Integer userId, Integer consigneeId);

	public ConsigneeModel addConsignee(ConsigneeModel consignee);

	public ConsigneeModel modifyConsignee(ConsigneeModel consignee);

	public void removeConsignee(Integer userId, Integer consigneeId);

	public void listConsignees(Integer userId);

	public void setPersonalInformation(Integer userId, String field, Object value);

	public UserDetailDto getPersonalInformation(Integer userId);

	public void resetpwd(Integer userId, String oldpwd, String newpwd);

}