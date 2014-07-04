
package com.flyhz.shop.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.flyhz.framework.lang.CacheRepository;
import com.flyhz.framework.lang.ValidateException;
import com.flyhz.framework.lang.file.FileRepository;
import com.flyhz.framework.util.Constants;
import com.flyhz.framework.util.JSONUtil;
import com.flyhz.shop.persistence.dao.VersionDao;
import com.flyhz.shop.persistence.entity.VersionModel;
import com.flyhz.shop.service.VersionService;

@Service
public class VersionServiceImpl implements VersionService {
	@Resource
	private VersionDao		versionDao;
	@Resource
	private FileRepository	fileRepository;
	@Resource
	private CacheRepository	cacheRepository;

	public void insertVersion(VersionModel versionModel, MultipartFile file)
			throws ValidateException {
		if (versionModel == null) {
			throw new ValidateException(710000);
		}
		if (file == null) {
			throw new ValidateException(710001);
		}
		if (versionModel.getVersionNew() == null) {
			throw new ValidateException(710002);
		}

		if (versionModel.getVersionLog() != null && versionModel.getVersionLog().length() > 500) {
			throw new ValidateException(710003);
		}
		// 保存apk文件
		try {
			if (file != null) {
				// 生成新文件名
				String origName = file.getOriginalFilename();
				origName = "apk/smile_" + versionModel.getVersionNew()
						+ origName.substring(origName.lastIndexOf("."));
				fileRepository.saveToTemp(file.getInputStream(), origName);
				versionModel.setVersionApk("/" + origName);
			}

		} catch (Exception e) {
			// 文件保存失败
			e.printStackTrace();
			throw new ValidateException(710004);
		}

		Date date = new Date();
		versionModel.setGmtCreate(date);
		versionModel.setGmtModify(date);
		versionDao.insert(versionModel);
		
		//redis设置版本号
		cacheRepository.setString(Constants.REDIS_KEY_VERSION, JSONUtil.getEntity2Json(versionModel));
	}

	public VersionModel getLastestModel() {
		return versionDao.getLastestModel();
	}

}
