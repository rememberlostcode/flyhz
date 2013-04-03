
package com.flyhz.shop.persistence;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.flyhz.framework.lang.CacheRepository;
import com.flyhz.framework.lang.RedisRepository;
import com.flyhz.framework.lang.ValidateException;
import com.flyhz.framework.util.Constants;
import com.flyhz.framework.util.JSONUtil;
import com.flyhz.framework.util.StringUtil;
import com.flyhz.shop.dto.BrandDto;
import com.flyhz.shop.dto.OrderDto;
import com.flyhz.shop.dto.ProductBuildDto;
import com.flyhz.shop.dto.ProductDto;
import com.flyhz.shop.persistence.dao.ProductDao;
import com.flyhz.shop.persistence.entity.BrandModel;
import com.flyhz.shop.persistence.entity.ProductModel;
import com.flyhz.shop.service.OrderService;

@Service
public class RedisRepositoryImpl implements RedisRepository {
	@Resource
	private CacheRepository	cacheRepository;
	@Resource
	private OrderService	orderService;
	@Resource
	private ProductDao		productDao;

	@Override
	public ProductDto getProductFromRedis(String productId) throws ValidateException {
		// TODO Auto-generated method stub
		if (StringUtil.isBlank(productId)) {
			throw new ValidateException("商品ID不能为空！");
		}
		ProductDto productDto = new ProductDto();
		String productJson = cacheRepository.getString(productId, ProductBuildDto.class);
		if (StringUtil.isBlank(productJson))
			return null;
		ProductBuildDto productBuildDto = JSONUtil.getJson2Entity(productJson,
				ProductBuildDto.class);
		if (productBuildDto == null) {
			ProductModel productModel = productDao.getModelById(Integer.parseInt(productId));
			if (productModel != null && productModel.getBrandId() != null) {
				String brandJson = cacheRepository.hget(Constants.REDIS_KEY_BRANDS,
						String.valueOf(productModel.getBrandId()));
				BrandModel brandModel = JSONUtil.getJson2Entity(brandJson, BrandModel.class);
				if (brandModel != null) {
					productDto.setId(productModel.getId());
					productDto.setName(productModel.getName());
					productDto.setPurchasingPrice(productModel.getPurchasingprice());
					productDto.setImgs(productModel.getImgs());
					BrandDto brand = new BrandDto();
					brand.setId(brandModel.getId());
					brand.setName(brandModel.getName());
					productDto.setBrand(brand);
				}
			} else {
				return null;
			}
		} else {
			productDto.setId(productBuildDto.getId());
			productDto.setName(productBuildDto.getN());
			productDto.setPurchasingPrice(productBuildDto.getPp());
			productDto.setImgs(productBuildDto.getP());
			BrandDto brand = new BrandDto();
			brand.setId(productBuildDto.getBid());
			brand.setName(productBuildDto.getBe());
			productDto.setBrand(brand);
		}
		return productDto;
	}

	@Override
	public OrderDto getOrderFromRedis(Integer userId, Integer orderId) throws ValidateException {
		// TODO Auto-generated method stub
		if (userId == null) {
			throw new ValidateException("用户ID不能为空！");
		}
		if (orderId == null) {
			throw new ValidateException("订单ID不能为空！");
		}
		String orderJson = cacheRepository.hget(Constants.REDIS_KEY_ORDERS_USER + "@" + userId,
				String.valueOf(orderId));
		OrderDto orderDto = JSONUtil.getJson2Entity(orderJson, OrderDto.class);
		if (orderDto == null) {
			orderDto = orderService.getOrder(userId, orderId);
			if (orderDto != null && orderDto.getId() != null) {
				cacheRepository.hset(Constants.REDIS_KEY_ORDERS_USER + "@" + userId,
						String.valueOf(orderDto.getId()), JSONUtil.getEntity2Json(orderDto));
			}
		}
		return orderDto;
	}

	@Override
	public void buildOrderToRedis(Integer userId, OrderDto orderDto) throws ValidateException {
		// TODO Auto-generated method stub
		if (userId != null && orderDto != null && orderDto.getId() != null) {
			cacheRepository.hset(Constants.REDIS_KEY_ORDERS_USER + "@" + userId,
					String.valueOf(orderDto.getId()), JSONUtil.getEntity2Json(orderDto));
			cacheRepository.lpush(Constants.REDIS_KEY_ORDERS_UNFINISHED + "@" + userId,
					String.valueOf(orderDto.getId()));
		} else {
			if (userId == null) {
				throw new ValidateException("用户ID不能为空！");
			}
			if (orderDto == null) {
				throw new ValidateException("订单不能为空！");
			}
			if (orderDto.getId() == null) {
				throw new ValidateException("订单ID不能为空！");
			}
		}
	}

	@Override
	public void reBuildOrderToRedis(Integer userId, OrderDto orderDto) throws ValidateException {
		// TODO Auto-generated method stub
		if (userId != null && orderDto != null && orderDto.getId() != null) {
			cacheRepository.lrem(Constants.REDIS_KEY_ORDERS_UNFINISHED + "@" + userId,
					String.valueOf(orderDto.getId()));
		} else {
			if (userId == null) {
				throw new ValidateException("用户ID不能为空！");
			}
			if (orderDto == null) {
				throw new ValidateException("订单不能为空！");
			}
			if (orderDto.getId() == null) {
				throw new ValidateException("订单ID不能为空！");
			}
		}
	}
}
