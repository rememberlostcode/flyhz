
package com.flyhz.shop.service;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.shop.dto.UserDto;

public interface UserService {
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
	 * 
	 * @param username
	 *            用户名
	 * @param token
	 *            用户token
	 * @param verifycode
	 *            保留
	 * @return
	 * @throws ValidateException
	 */
	public UserDto logout(String username, String token, String verifycode)
			throws ValidateException;
}