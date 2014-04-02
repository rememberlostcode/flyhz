
package com.flyhz.shop.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.flyhz.framework.lang.CacheRepository;
import com.flyhz.framework.util.JSONUtil;
import com.flyhz.shop.dto.BrandDto;
import com.flyhz.shop.dto.ProductBuildDto;
import com.flyhz.shop.dto.ProductDto;
import com.flyhz.shop.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {
	@Resource
	private CacheRepository	cacheRepository;

	@Override
	public ProductDto getProductFromRedis(String productId) {
		ProductDto productDto = new ProductDto();
		String productJson = cacheRepository.getString(productId, ProductBuildDto.class);
		ProductBuildDto productBuildDto = JSONUtil.getJson2Entity(productJson,
				ProductBuildDto.class);
		productDto.setId(productBuildDto.getId());
		productDto.setName(productBuildDto.getN());
		productDto.setPurchasingPrice(productBuildDto.getPp());
		productDto.setImgs(productBuildDto.getP());
		BrandDto brand = new BrandDto();
		brand.setId(productBuildDto.getBid());
		brand.setName(productBuildDto.getBe());
		productDto.setBrand(brand);
		return productDto;
	}
}
