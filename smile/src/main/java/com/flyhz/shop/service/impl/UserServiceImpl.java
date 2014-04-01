
package com.flyhz.shop.service.impl;

import org.springframework.stereotype.Service;

import com.flyhz.shop.dto.Consignee;
import com.flyhz.shop.dto.User;
import com.flyhz.shop.dto.UserDetail;
import com.flyhz.shop.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Override
	public User register(UserDetail userDetail) {
		return null;
	}

	@Override
	public User login(String username, String passwod, String verifycode) {
		return null;
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