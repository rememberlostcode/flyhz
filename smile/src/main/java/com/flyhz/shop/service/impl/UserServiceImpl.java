
package com.flyhz.shop.service.impl;

import org.springframework.stereotype.Service;

import com.flyhz.shop.dto.UserDetailDto;
import com.flyhz.shop.dto.UserDto;
import com.flyhz.shop.persistence.entity.ConsigneeModel;
import com.flyhz.shop.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	public UserDto register(UserDetailDto userDetail) {
		return null;
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