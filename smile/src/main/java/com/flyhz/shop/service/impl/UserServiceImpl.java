
package com.flyhz.shop.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.framework.util.StringUtil;
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
			throw new ValidateException("用户名与密码不能为空");
		if (StringUtils.isBlank(userDetail.getUsername()))
			throw new ValidateException("用户名为空");
		if (StringUtil.stringLength(userDetail.getUsername()) > 32)
			throw new ValidateException("用户名长度不能大于32个字节");
		UserModel user = userDao.getUserByName(userDetail.getUsername());
		if (user != null)
			throw new ValidateException("用户名已存在");

		if (StringUtils.isBlank(userDetail.getPassword()))
			throw new ValidateException("密码为空");

		UserModel userModel = new UserModel();
		userModel.setUsername(userDetail.getUsername());
		userModel.setPassword(userDetail.getPassword());
		if (StringUtils.isNotBlank(userDetail.getEmail())) {
			if (StringUtil.validEmail(userDetail.getEmail())) {
				userModel.setEmail(userDetail.getEmail());
			} else {
				throw new ValidateException("电子邮箱输入不正确");
			}
		}
		userModel.setGmtCreate(new Date());
		userModel.setGmtModify(new Date());
		userModel.setGmtRegister(new Date());
		userDao.register(userModel);

		user = userDao.getUserByName(userDetail.getUsername());
		UserDto userDto = new UserDto();
		userDto.setId(user.getId());
		userDto.setUsername(user.getUsername());
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