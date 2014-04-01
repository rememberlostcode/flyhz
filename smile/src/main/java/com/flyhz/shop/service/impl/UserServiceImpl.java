
package com.flyhz.shop.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.framework.util.RandomString;
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

	@Override
	public UserDto register(UserDetailDto userDetail) {
		return null;
	}

	@Override
	public UserDto login(String username, String password, String verifycode) {
		UserModel userTemp = new UserModel();
		userTemp.setUsername(username);
		userTemp.setPassword(password);
		UserModel userModel = userDao.getModel(userTemp);
		if (userModel == null) {
			return null;
		} else {
			String token = RandomString.generateRandomString16();
			userModel.setToken(token);
			userDao.update(userModel);
			UserDto user = new UserDto();
			user.setId(userModel.getId());
			user.setUsername(userModel.getUsername());
			user.setToken(token);
			return user;
		}
	}

	@Override
	public UserDto loginAuto(Integer userId, String token, String verifycode)
			throws ValidateException {
		UserModel userTemp = new UserModel();
		userTemp.setId(userId);
		userTemp.setToken(token);
		UserModel userModel = userDao.getModel(userTemp);
		if (userModel == null) {
			return null;
		} else {
			UserDto user = new UserDto();
			user.setId(userModel.getId());
			user.setUsername(userModel.getUsername());
			user.setToken(token);
			return user;
		}
	}

	@Override
	public void logout(Integer userId) {
		UserModel userTemp = new UserModel();
		userTemp.setId(userId);
		UserModel userModel = userDao.getModel(userTemp);
		if (userModel != null) {
			userModel.setToken(null);
			userDao.update(userModel);
		}
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