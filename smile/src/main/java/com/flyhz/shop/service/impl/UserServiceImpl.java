
package com.flyhz.shop.service.impl;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.shop.dto.UserDetailDto;
import com.flyhz.shop.dto.UserDto;
import com.flyhz.shop.persistence.dao.UserDao;
import com.flyhz.shop.persistence.entity.ConsigneeModel;
import com.flyhz.shop.persistence.entity.UserModel;
import com.flyhz.shop.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	@Resource
	private UserDao	userDao;

	public UserDto register(UserDetailDto userDetail) throws ValidateException {
		if (userDetail == null)
			throw new ValidateException("");
		if (StringUtils.isBlank(userDetail.getUsername()))
			throw new ValidateException("");
		if (StringUtils.isBlank(userDetail.getPassword()))
			throw new ValidateException("");
		UserModel userModel = new UserModel();
		userModel.setUsername(userDetail.getUsername());
		userModel.setPassword(userDetail.getPassword());
		if (StringUtils.isNotBlank(userDetail.getEmail())) {

		}
		userModel.setEmail(userDetail.getEmail());
		userDao.register(userModel);
		UserDto userDto = new UserDto();
		userDto.setId(userModel.getId());
		userDto.setUsername(userModel.getUsername());
		return userDto;
	}

	@Override
	public UserDto login(String username, String passwod, String verifycode) {
		return null;
	}

	@Override
	public void logout(Integer userId) {

	}

	@Override
	public ConsigneeModel getConsignee(Integer userId, Integer consigneeId) {
		return null;
	}

	@Override
	public ConsigneeModel addConsignee(ConsigneeModel consignee) {
		return null;
	}

	@Override
	public ConsigneeModel modifyConsignee(ConsigneeModel consignee) {
		return null;
	}

	@Override
	public void removeConsignee(Integer userId, Integer consigneeId) {

	}

	@Override
	public void listConsignees(Integer userId) {

	}

	@Override
	public void setPersonalInformation(Integer userId, String field, Object value) {

	}

	@Override
	public UserDetailDto getPersonalInformation(Integer userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resetpwd(Integer userId, String oldpwd, String newpwd) {
		// TODO Auto-generated method stub

	}
}