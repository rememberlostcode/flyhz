
package com.flyhz.shop.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.flyhz.shop.dto.BrandBuildDto;
import com.flyhz.shop.persistence.dao.BrandDao;
import com.flyhz.shop.service.BrandService;

@Service
public class BrandServiceImpl implements BrandService {
	@Resource
	private BrandDao	brandDao;

	@Override
	public List<BrandBuildDto> getAllBrandBuildDtos() {
		return brandDao.getBrandBuildDtoList();
	}
}
