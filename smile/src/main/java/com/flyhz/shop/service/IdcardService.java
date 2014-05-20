
package com.flyhz.shop.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.shop.persistence.entity.IdcardModel;

public interface IdcardService {

	public List<IdcardModel> getAllIdcardsByUserId(Integer userId) throws ValidateException;

	public void saveIdcard(IdcardModel idcardModel, MultipartFile multipartFile)
			throws ValidateException;

	public void deleteIdcard(Integer id, Integer userId) throws ValidateException;

	public IdcardModel getIdcardModelById(Integer id, Integer userId) throws ValidateException;
}
