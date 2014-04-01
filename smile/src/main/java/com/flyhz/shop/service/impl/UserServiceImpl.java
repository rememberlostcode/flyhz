
package com.flyhz.shop.service.impl;

import javax.annotation.Resource;

import com.flyhz.framework.util.RandomString;
import com.flyhz.shop.dto.Consignee;
import com.flyhz.shop.dto.User;
import com.flyhz.shop.dto.UserDetail;
import com.flyhz.shop.persistence.dao.UserDao;
import com.flyhz.shop.service.UserService;

public class UserServiceImpl implements UserService {
	@Resource
	private UserDao	userDao;

	@Override
	public User register(UserDetail userDetail) {
		return null;
	}

	@Override
	public User login(String username, String password, String verifycode) {
		com.flyhz.shop.persistence.entity.User userTemp = new com.flyhz.shop.persistence.entity.User();
		userTemp.setUsername(username);
		userTemp.setPassword(password);
		com.flyhz.shop.persistence.entity.User userModel = userDao.getModel(userTemp);
		if (userModel == null) {
			return null;
		} else {
			String token = RandomString.generateRandomString16();
			userModel.setToken(token);
			userDao.update(userModel);
			User user = new User();
			user.setId(userModel.getId());
			user.setUsername(userModel.getUsername());
			user.setToken(token);
			return user;
		}
	}

	@Override
	public void logout(Integer userId) {
		// TODO Auto-generated method stub

	}

	@Override
	public Consignee getConsignee(Integer userId, Integer consigneeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Consignee addConsignee(Consignee consignee) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Consignee modifyConsignee(Consignee consignee) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeConsignee(Integer userId, Integer consigneeId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void listConsignees(Integer userId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPersonalInformation(Integer userId, String field, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public UserDetail getPersonalInformation(Integer userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resetpwd(Integer userId, String oldpwd, String newpwd) {
		// TODO Auto-generated method stub

	}
}