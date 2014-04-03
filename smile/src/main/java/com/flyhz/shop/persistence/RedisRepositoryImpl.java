
package com.flyhz.shop.persistence;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.flyhz.framework.lang.CacheRepository;
import com.flyhz.framework.lang.RedisRepository;
import com.flyhz.framework.util.JSONUtil;
import com.flyhz.shop.dto.BrandDto;
import com.flyhz.shop.dto.ProductBuildDto;
import com.flyhz.shop.dto.ProductDto;

@Service
public class RedisRepositoryImpl implements RedisRepository {
	@Resource
	private CacheRepository	cacheRepository;

	@Override
	public ProductDto getProductFromRedis(String productId) {
		ProductDto productDto = new ProductDto();
		String productJson = cacheRepository.getString(productId, ProductBuildDto.class);
		ProductBuildDto productBuildDto = JSONUtil.getJson2Entity(productJson,
				ProductBuildDto.class);
		if (productBuildDto == null)
			return null;

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
