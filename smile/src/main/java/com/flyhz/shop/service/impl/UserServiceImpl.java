
package com.flyhz.shop.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.framework.lang.file.FileRepository;
import com.flyhz.framework.lang.mail.MailRepository;
import com.flyhz.framework.util.DateUtil;
import com.flyhz.framework.util.MD5;
import com.flyhz.framework.util.RandomString;
import com.flyhz.framework.util.RegexUtils;
import com.flyhz.framework.util.StringUtil;
import com.flyhz.framework.util.ValidateUtil;
import com.flyhz.shop.dto.ConsigneeDetailDto;
import com.flyhz.shop.dto.ConsigneeDto;
import com.flyhz.shop.dto.UserDetailDto;
import com.flyhz.shop.dto.UserDto;
import com.flyhz.shop.persistence.dao.ConsigneeDao;
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
	@Resource
	private FileRepository	fileRepository;
	@Resource
	private MailRepository	mailRepository;

	@Override
	public UserDto register(UserDetailDto userDetail) throws ValidateException {
		if (userDetail == null)
			throw new ValidateException(101029);
		if (StringUtils.isBlank(userDetail.getUsername()))
			throw new ValidateException(101030);
		if (StringUtil.stringLength(userDetail.getUsername()) > 32)
			throw new ValidateException(101031);
		UserModel user = userDao.getUserByName(userDetail.getUsername());
		if (user != null)
			throw new ValidateException(101032);

		if (StringUtils.isBlank(userDetail.getPassword()))
			throw new ValidateException(101033);

		UserModel userModel = new UserModel();
		userModel.setUsername(userDetail.getUsername());
		userModel.setPassword(MD5.getMD5(userDetail.getPassword()));
		if (StringUtils.isNotBlank(userDetail.getEmail())) {
			if (RegexUtils.checkEmail(userDetail.getEmail())) {
				userModel.setEmail(userDetail.getEmail());
			} else {
				throw new ValidateException(101017);
			}
		}
		userModel.setGmtCreate(new Date());
		userModel.setGmtModify(new Date());
		userModel.setGmtRegister(new Date());
		userDao.register(userModel);

		UserDto userDto = new UserDto();
		userDto.setId(userModel.getId());
		userDto.setUsername(userModel.getUsername());
		return userDto;
	}

	@Override
	public UserDto login(String username, String password, String verifycode)
			throws ValidateException {
		if (StringUtils.isBlank(username)) {
			throw new ValidateException(101030);// 用户名不能为空
		}
		if (StringUtils.isBlank(password)) {
			throw new ValidateException(101033);// 密码不能为空
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
	public UserDto loginAuto(String username, String token, String verifycode)
			throws ValidateException {
		if (username == null) {
			throw new ValidateException(101002);// 用户名不能为空
		}
		if (StringUtils.isBlank(token)) {
			throw new ValidateException(101034);// 用户TOKEN不能为空
		}
		UserModel userTemp = new UserModel();
		userTemp.setUsername(username);
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
	public UserDto logout(String username, String token, String verifycode)
			throws ValidateException {
		if (username == null) {
			throw new ValidateException(101002);// 用户ID不能为空
		}
		UserModel userTemp = new UserModel();
		userTemp.setUsername(username);
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
		// 收件人对象不能为空
		if (consignee == null) {
			throw new ValidateException(101001);
		}
		// 登陆用户ID不能为空
		if (consignee.getUserId() == null) {
			throw new ValidateException(101002);
		}
		// 收件人姓名不能为空
		if (StringUtil.isBlank(consignee.getName())) {
			throw new ValidateException(101003);
		}
		// 收件人姓名最大为16个字符
		if (StringUtil.length(consignee.getName()) > 16) {
			throw new ValidateException(101004);
		}
		// 收件人国家不能为空
		if (consignee.getConturyId() == null) {
			throw new ValidateException(101005);
		}
		// 收件人省份不能为空
		if (consignee.getProvinceId() == null) {
			throw new ValidateException(101006);
		}
		// 收件人市不能为空
		if (consignee.getCityId() == null) {
			throw new ValidateException(101007);
		}
		// 收件人区不能为空
		if (consignee.getDistrictId() == null) {
			throw new ValidateException(101008);
		}
		// 收件人街道地址不能为空
		if (StringUtil.isBlank(consignee.getAddress())) {
			throw new ValidateException(101009);
		}
		// 收件人街道地址最大128个字符
		if (StringUtil.length(consignee.getAddress()) > 128) {
			throw new ValidateException(101010);
		}
		// 邮编不能为空
		if (StringUtil.isBlank(consignee.getZipcode())) {
			throw new ValidateException(101011);
		}
		// 邮编格式错误
		if (!ValidateUtil.isValidZipcode(consignee.getZipcode())) {
			throw new ValidateException(101012);
		}
		// 收件人手机号不能为空
		if (StringUtil.isBlank(consignee.getMobilephone())) {
			throw new ValidateException(101013);
		}
		// 手机号码格式错误
		if (!ValidateUtil.isMobileNO(consignee.getMobilephone())) {
			throw new ValidateException(101014);
		}
		consigneeDao.insertConsignee(consignee);
		return consignee;
	}

	@Override
	public ConsigneeModel modifyConsignee(ConsigneeModel consignee) throws ValidateException {
		// 收件人对象不能为空
		if (consignee == null || consignee.getId() == null) {
			throw new ValidateException(101001);
		}
		// 登陆用户ID不能为空
		if (consignee.getUserId() == null) {
			throw new ValidateException(101002);
		}
		// 收件人姓名不能为空
		if (StringUtil.isBlank(consignee.getName())) {
			throw new ValidateException(101003);
		}
		// 收件人姓名最大为16个字符
		if (StringUtil.length(consignee.getName()) > 16) {
			throw new ValidateException(101004);
		}
		// 收件人国家不能为空
		if (consignee.getConturyId() == null) {
			throw new ValidateException(101005);
		}
		// 收件人省份不能为空
		if (consignee.getProvinceId() == null) {
			throw new ValidateException(101006);
		}
		// 收件人市不能为空
		if (consignee.getCityId() == null) {
			throw new ValidateException(101007);
		}
		// 收件人区不能为空
		if (consignee.getDistrictId() == null) {
			throw new ValidateException(101008);
		}
		// 收件人街道地址不能为空
		if (StringUtil.isBlank(consignee.getAddress())) {
			throw new ValidateException(101009);
		}
		// 收件人街道地址最大128个字符
		if (StringUtil.length(consignee.getAddress()) > 128) {
			throw new ValidateException(101010);
		}
		// 邮编不能为空
		if (StringUtil.isBlank(consignee.getZipcode())) {
			throw new ValidateException(101011);
		}
		// 邮编格式错误
		if (!ValidateUtil.isValidZipcode(consignee.getZipcode())) {
			throw new ValidateException(101012);
		}
		// 收件人手机号不能为空
		if (StringUtil.isBlank(consignee.getMobilephone())) {
			throw new ValidateException(101013);
		}
		// 手机号码格式错误
		if (!ValidateUtil.isMobileNO(consignee.getMobilephone())) {
			throw new ValidateException(101014);
		}
		// 查询旧收件人地址
		ConsigneeModel consigneeModelOld = consigneeDao.getModel(consignee);
		if (consigneeModelOld == null) {
			throw new ValidateException(101001);
		}
		consigneeDao.updateConsignee(consignee);
		return consignee;
	}

	@Override
	public void removeConsignee(Integer userId, Integer consigneeId) throws ValidateException {
		// 登陆用户ID不能为空
		if (userId == null) {
			throw new ValidateException(101002);
		}
		// 收件人地址ID不能为空
		if (consigneeId == null) {
			throw new ValidateException(101001);
		}
		ConsigneeModel consigneeModel = new ConsigneeModel();
		consigneeModel.setId(consigneeId);
		consigneeModel.setUserId(userId);
		consigneeModel = consigneeDao.getModel(consigneeModel);
		if (consigneeModel == null) {
			throw new ValidateException(101001);
		}
		consigneeDao.deleteConsignee(consigneeModel);
	}

	@Override
	public List<ConsigneeDetailDto> listConsignees(Integer userId) {
		if (userId == null)
			return null;
		UserModel user = userDao.getModelById(userId);
		if (user != null) {
			List<ConsigneeDetailDto> consigneeList = consigneeDao.getConsigneesByUserId(userId);
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
		// 登陆用户ID不能为空
		if (userId == null) {
			throw new ValidateException(101002);
		}
		// 待修改用户属性不能为空
		if (StringUtils.isBlank(field)) {
			throw new ValidateException(101015);
		}
		UserModel userModel = new UserModel();
		userModel.setId(userId);
		userModel = userDao.getModel(userModel);
		// 登陆用户不能为空
		if (userModel == null) {
			throw new ValidateException(101002);
		}
		// 判断是更新电话、邮箱、密码
		if (field.equals("email")) {
			// 校验email格式是否正确
			String valueStr = String.valueOf(value);
			String emailOld = userModel.getEmail();
			if (StringUtil.isNotBlank(valueStr)) {
				// 邮箱长度最大为64个字符
				if (StringUtil.length(valueStr) > 64) {
					throw new ValidateException(101016);
				} else if (!ValidateUtil.checkEmail(valueStr)) {
					throw new ValidateException(101017);
				}
			}
			userModel.setEmail(valueStr);
			userModel.setGmtModify(new Date());
			userDao.updateEmail(userModel);
			// 新旧邮箱不同，发送校验邮件
			if (StringUtil.isNotBlank(valueStr) && valueStr.equalsIgnoreCase(emailOld)) {

			}
		} else if (field.equals("mphone")) {
			// 校验电话格式是否正确
			String valueStr = String.valueOf(value);
			if (StringUtil.isNotBlank(valueStr) && !ValidateUtil.isMobileNO(valueStr)) {
				throw new ValidateException(101014);
			}
			userModel.setMobilephone(valueStr);
			userModel.setGmtModify(new Date());
			userDao.updateMobilePhone(userModel);
		} else if (field.equals("pwd")) {
			// 密码不能设置为空
			// 密码长度最大为16个字符
			String valueStr = String.valueOf(value);
			if (StringUtil.isBlank(valueStr)) {
				throw new ValidateException(101018);
			} else if (StringUtil.length(valueStr) > 64) {
				throw new ValidateException(101019);
			}
			userModel.setPassword(MD5.getMD5(valueStr));
			userModel.setGmtModify(new Date());
			userDao.updatePwd(userModel);
		}
	}

	@Override
	public void setIdCardImg(Integer userId, Integer consigneeId, MultipartFile multipartFile)
			throws ValidateException {
		// 登陆用户ID不能为空
		if (userId == null) {
			throw new ValidateException(101002);
		}
		// 收件人地址ID不能为空
		if (consigneeId == null) {
			throw new ValidateException(101001);
		}
		ConsigneeModel consigneeModel = new ConsigneeModel();
		consigneeModel.setId(consigneeId);
		consigneeModel.setUserId(userId);
		consigneeModel = consigneeDao.getModel(consigneeModel);
		// 查询旧收件人地址
		if (consigneeModel == null) {
			throw new ValidateException(101001);
		}
		// 保存收件人地址身份证照片
		try {
			// 生成新文件名
			String origName = multipartFile.getOriginalFilename();
			origName = DateUtil.dateToStrMSec(new Date()) + consigneeId
					+ origName.substring(origName.lastIndexOf("."));
			fileRepository.saveToTemp(multipartFile.getInputStream(), origName);
			// 更新收件人地址中身份证照片路径
			consigneeModel.setIdcard(File.separator + origName);
			consigneeDao.updateConsigneeIdCard(consigneeModel);
		} catch (IOException e) {
			// 文件保存失败
			throw new ValidateException(101020);
		}
	}

	@Override
	public void relieveEmail(Integer userId) throws ValidateException {
		// 登陆用户ID不能为空
		if (userId == null) {
			throw new ValidateException(101002);
		}
		UserModel userModel = new UserModel();
		userModel.setId(userId);
		userModel = userDao.getModel(userModel);
		if (userModel == null) {
			throw new ValidateException(101002);
		}
		userModel.setEmail("");
		userDao.updateEmail(userModel);
	}

	@Override
	public UserDetailDto getPersonalInformation(Integer userId) throws ValidateException {
		if (userId == null) {
			throw new ValidateException(101002);
		}
		UserModel userModel = userDao.getModelById(userId);
		if (userModel == null) {
			throw new ValidateException(101002);
		}
		UserDetailDto userDetailDto = new UserDetailDto();
		BeanUtils.copyProperties(userModel, userDetailDto);
		return userDetailDto;
	}

	@Override
	public void resetpwd(Integer userId, String oldpwd, String newpwd) throws ValidateException {
		// 登陆用户ID不能为空
		if (userId == null) {
			throw new ValidateException(101002);
		}
		// 旧密码不能为空
		if (StringUtil.isBlank(oldpwd)) {
			throw new ValidateException(101024);
		} else if (StringUtil.length(oldpwd) > 64) {
			throw new ValidateException(101027);
		}
		// 新密码不能为空
		if (StringUtil.isBlank(newpwd)) {
			throw new ValidateException(101025);
		} else if (StringUtil.length(newpwd) > 64) {
			throw new ValidateException(101028);
		}
		UserModel userModel = new UserModel();
		userModel.setId(userId);
		userModel = userDao.getModel(userModel);
		// 登陆用户ID不能为空
		if (userModel == null) {
			throw new ValidateException(101002);
		}
		// 输入旧密码不对
		if (!MD5.getMD5(oldpwd).equals(userModel.getPassword())) {
			throw new ValidateException(101026);
		}
		userModel.setPassword(MD5.getMD5(newpwd));
		userDao.updatePwd(userModel);
	}
}