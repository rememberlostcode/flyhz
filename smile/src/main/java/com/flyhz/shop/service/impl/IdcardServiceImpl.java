
package com.flyhz.shop.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.framework.lang.file.FileRepository;
import com.flyhz.framework.util.DateUtil;
import com.flyhz.shop.persistence.dao.IdcardDao;
import com.flyhz.shop.persistence.entity.IdcardModel;
import com.flyhz.shop.service.IdcardService;

@Service
public class IdcardServiceImpl implements IdcardService {
	@Resource
	private IdcardDao		idcardDao;
	@Resource
	private FileRepository	fileRepository;

	@Override
	public List<IdcardModel> getAllIdcardsByUserId(Integer userId) {
		IdcardModel im = new IdcardModel();
		im.setUserId(userId);
		return idcardDao.getModelList(im);
	}

	@Override
	public void saveIdcard(IdcardModel idcardModel, MultipartFile photo,MultipartFile backPhoto)
			throws ValidateException {
		if (idcardModel == null) {
			throw new ValidateException(111110);
		}
		// 保存收件人地址身份证照片
		try {
			if (idcardModel.getId() == null && photo == null) {
				throw new ValidateException(120002);
			}
			if (photo != null) {
				// 生成新文件名
				String origName = photo.getOriginalFilename();
				origName = "idcard/" + DateUtil.dateToStrMSec(new Date())
						+ origName.substring(origName.lastIndexOf("."));
				fileRepository.saveToTarget(photo.getInputStream(), origName);
				// 更新收件人地址中身份证照片路径
				idcardModel.setUrl("/" + origName);
			}
			
			if (backPhoto != null) {
				// 生成新文件名
				String origName = backPhoto.getOriginalFilename();
				origName = "idcard/" + DateUtil.dateToStrMSec(new Date())
						+ origName.substring(origName.lastIndexOf("."));
				fileRepository.saveToTarget(backPhoto.getInputStream(), origName);
				// 更新收件人地址中身份证照片路径
				idcardModel.setBack_url("/" + origName);
			}

		} catch (Exception e) {
			// 文件保存失败
			throw new ValidateException(122222);
		}

		try {
			Date date = new Date();
			if (idcardModel != null && idcardModel.getId() != null) {
				idcardModel.setGmtModify(date);
				idcardDao.update(idcardModel);
			} else {
				idcardModel.setGmtCreate(date);
				idcardModel.setGmtModify(date);
				idcardDao.insertIdcard(idcardModel);
			}
		} catch (Exception e) {
			// 文件保存失败
			throw new ValidateException(122221);
		}
	}

	@Override
	public void deleteIdcard(Integer id, Integer userId) throws ValidateException {
		if (id == null) {
			throw new ValidateException(111111);
		}
		if (userId == null) {
			throw new ValidateException(101002);
		}
		IdcardModel im = new IdcardModel();
		im.setId(id);
		im.setUserId(userId);
		idcardDao.delete(im);
	}

	@Override
	public IdcardModel getIdcardModelById(Integer id, Integer userId) throws ValidateException {
		if (id == null) {
			throw new ValidateException(111111);
		}
		if (userId == null) {
			throw new ValidateException(101002);
		}
		return idcardDao.getModelById(id);
	}

}
