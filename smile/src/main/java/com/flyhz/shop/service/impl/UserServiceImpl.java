
package com.flyhz.shop.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.framework.util.RandomString;
import com.flyhz.framework.util.RegexUtils;
import com.flyhz.framework.util.StringUtil;
import com.flyhz.framework.util.ValidateUtil;
import com.flyhz.shop.dto.ConsigneeDto;
import com.flyhz.shop.dto.UserDetailDto;
import com.flyhz.shop.dto.UserDto;
import com.flyhz.shop.persistence.dao.UserDao;
import com.flyhz.shop.persistence.entity.ConsigneeModel;
import com.flyhz.shop.persistence.entity.UserModel;
import com.flyhz.shop.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	@Resource
	private UserDao			userDao;
	@Resource
	private ConsigneeDao	consigneeDao;

	@Override
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
			if (RegexUtils.checkEmail(userDetail.getEmail())) {
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
	public UserDto login(String username, String password, String verifycode)
			throws ValidateException {
		if (StringUtils.isBlank(username)) {
			throw new ValidateException("");// 用户名不能为空
		}
		if (StringUtils.isBlank(password)) {
			throw new ValidateException("");// 密码不能为空
		}
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
		if (userId == null) {
			throw new ValidateException("");// 用户ID不能为空
		}
		if (StringUtils.isBlank(token)) {
			throw new ValidateException("");// 用户TOKEN不能为空
		}
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
	public UserDto logout(Integer userId, String token, String verifycode) throws ValidateException {
		if (userId == null) {
			throw new ValidateException("");// 用户ID不能为空
		}
		UserModel userTemp = new UserModel();
		userTemp.setId(userId);
		userTemp.setToken(token);
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

	@Override
	public ConsigneeModel getConsignee(Integer userId, Integer consigneeId) {
		ConsigneeModel model = new ConsigneeModel();
		model.setId(consigneeId);
		model.setUserId(userId);
		return consigneeDao.getModel(model);
	}

	@Override
	public ConsigneeModel addConsignee(ConsigneeModel consignee) throws ValidateException {
		if (consignee == null) {
			throw new ValidateException("");
		}
		if (StringUtils.isBlank(consignee.getName())) {

		}
		return null;
	}

	@Override
	public ConsigneeModel modifyConsignee(ConsigneeModel consignee) throws ValidateException {
		if (consignee == null) {
			throw new ValidateException("");
		}
		return null;
	}

	@Override
	public void removeConsignee(Integer userId, Integer consigneeId) throws ValidateException {
		if (userId == null) {
			throw new ValidateException("");
		}
		if (consigneeId == null) {
			throw new ValidateException("");
		}
		// ConsigneeModel consigneeModel
	}

	@Override
	public List<ConsigneeDto> listConsignees(Integer userId) {
		if (userId == null)
			return null;
		UserModel user = userDao.getModelById(userId);
		if (user != null) {
			List<ConsigneeDto> consigneeList = consigneeDao.getConsigneesByUserId(userId);
			if (consigneeList != null && !consigneeList.isEmpty()) {
				for (ConsigneeDto consignee : consigneeList) {
					UserDto userDto = new UserDto();
					BeanUtils.copyProperties(user, userDto);
					consignee.setUser(userDto);
				}
				return consigneeList;
			}
		}
		return null;
	}

	@Override
	public void setPersonalInformation(Integer userId, String field, Object value)
			throws ValidateException {
		if (userId == null) {
			throw new ValidateException("");
		}
		if (StringUtils.isBlank(field)) {
			throw new ValidateException("");
		}
		UserModel userModel = new UserModel();
		userModel.setId(userId);
		userModel = userDao.getModel(userModel);
		if (userModel == null) {
			throw new ValidateException("");
		}
		// 判断是更新电话、邮箱、密码
		if (field.equals("email")) {
			// 校验email格式是否正确
			String valueStr = String.valueOf(value);
			if (StringUtil.isNotBlank(valueStr)) {
				// 邮箱长度最大为64个英文字符
				if (StringUtil.length(valueStr) > 64) {
					throw new ValidateException("");
				} else if (!ValidateUtil.checkEmail(valueStr)) {
					throw new ValidateException("");
				}
			}
			userModel.setEmail(valueStr);
			userDao.updateEmail(userModel);
		} else if (field.equals("mphone")) {
			// 校验电话格式是否正确
			String valueStr = String.valueOf(value);
			if (StringUtil.isNotBlank(valueStr) && !ValidateUtil.isMobileNO(valueStr)) {
				throw new ValidateException("");
			}
			userModel.setMobilephone(valueStr);
			userDao.updateMobilePhone(userModel);
		} else if (field.equals("pwd")) {
			// 密码不能设置为空
			// 密码长度最大为16个英文字符
			String valueStr = String.valueOf(value);
			if (StringUtil.isBlank(valueStr)) {
				throw new ValidateException("");
			} else if (StringUtil.length(valueStr) > 16) {
				throw new ValidateException("");
			}
			userModel.setPassword(valueStr);
			userDao.updatePwd(userModel);
		}
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