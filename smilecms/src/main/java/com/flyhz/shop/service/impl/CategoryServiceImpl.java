
package com.flyhz.shop.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.flyhz.shop.dto.CategoryBuildDto;
import com.flyhz.shop.persistence.dao.CategoryDao;
import com.flyhz.shop.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {
	@Resource
	private CategoryDao	categoryDao;

	@Override
	public List<CategoryBuildDto> getAllCategoryBuildDtos() {
		return categoryDao.getCategoryBuildDtoList();
	}
}
