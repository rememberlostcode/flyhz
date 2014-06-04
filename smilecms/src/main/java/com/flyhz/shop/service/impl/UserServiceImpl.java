
package com.flyhz.shop.service.impl;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.framework.util.MD5;
import com.flyhz.framework.util.RandomString;
import com.flyhz.shop.dto.UserDto;
import com.flyhz.shop.persistence.dao.UserDao;
import com.flyhz.shop.persistence.entity.UserModel;
import com.flyhz.shop.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	@Resource
	private UserDao	userDao;

	@Override
	public UserDto login(String username, String password, String verifycode)
			throws ValidateException {
		if (StringUtils.isBlank(username)) {
			throw new ValidateException(140002);// 用户名不能为空
		}
		if (StringUtils.isBlank(password)) {
			throw new ValidateException(140005);// 密码不能为空
		}
		UserModel userTemp = new UserModel();
		userTemp.setUsername(username);
		userTemp.setPassword(MD5.getMD5(password));
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
	public UserDto logout(String username, String token, String verifycode)
			throws ValidateException {
		if (username == null) {
			throw new ValidateException(140002);// 用户名不能为空
		}
		UserModel userTemp = new UserModel();
		userTemp.setUsername(username);
		UserModel userModel = userDao.getModel(userTemp);
		if (userModel == null) {
			return null;
		} else {
			userModel.setToken(null);
			userDao.update(userModel);
			UserDto user = new UserDto();
			user.setId(userModel.getId());
			user.setUsername(userModel.getUsername());
			return user;
		}
	}
}