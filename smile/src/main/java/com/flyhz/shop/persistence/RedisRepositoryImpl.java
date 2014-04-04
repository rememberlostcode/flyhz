
package com.flyhz.shop.persistence;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.flyhz.framework.lang.CacheRepository;
import com.flyhz.framework.lang.RedisRepository;
import com.flyhz.framework.lang.ValidateException;
import com.flyhz.framework.util.Constants;
import com.flyhz.framework.util.JSONUtil;
import com.flyhz.framework.util.StringUtil;
import com.flyhz.shop.dto.BrandBuildDto;
import com.flyhz.shop.dto.BrandDto;
import com.flyhz.shop.dto.ProductBuildDto;
import com.flyhz.shop.dto.ProductDto;
import com.flyhz.shop.persistence.dao.OrderDao;
import com.flyhz.shop.persistence.dao.ProductDao;
import com.flyhz.shop.persistence.entity.OrderModel;
import com.flyhz.shop.persistence.entity.ProductModel;

@Service
public class RedisRepositoryImpl implements RedisRepository {
	private Logger			log	= LoggerFactory.getLogger(RedisRepositoryImpl.class);
	@Resource
	private CacheRepository	cacheRepository;
	@Resource
	private OrderDao		orderDao;
	@Resource
	private ProductDao		productDao;

	@Override
	public ProductDto getProductFromRedis(String productId) throws ValidateException {
		// TODO Auto-generated method stub
		if (StringUtil.isBlank(productId)) {
			throw new ValidateException("商品ID不能为空！");
		}
		ProductDto productDto = new ProductDto();
		String productJson = cacheRepository.hget(Constants.REDIS_KEY_PRODUCTS, productId);
		if (StringUtil.isBlank(productJson)) {
			log.warn("ID为" + productId + "的商品在redis中不存在！");
			return null;
		}
		ProductBuildDto productBuildDto = JSONUtil.getJson2Entity(productJson,
				ProductBuildDto.class);
		if (productBuildDto == null) {// 如果为null，先查找数据库，如存在再更新redis
			ProductModel productModel = productDao.getModelById(Integer.parseInt(productId));
			if (productModel != null && productModel.getBrandId() != null) {

				// 通过品牌ID获取品牌信息
				String brandJson = cacheRepository.hget(Constants.REDIS_KEY_BRANDS,
						String.valueOf(productModel.getBrandId()));
				BrandBuildDto brandBuildDto = JSONUtil.getJson2Entity(brandJson,
						BrandBuildDto.class);

				// 如果品牌不为空，把商品信息和品牌信息放入productDto
				if (brandBuildDto != null) {
					productDto.setId(productModel.getId());
					productDto.setName(productModel.getName());
					productDto.setPurchasingPrice(productModel.getPurchasingprice());
					productDto.setImgs(productModel.getImgs());
					BrandDto brand = new BrandDto();
					brand.setId(brandBuildDto.getId());
					brand.setName(brandBuildDto.getName());
					productDto.setBrand(brand);
				}
			} else {
				log.warn("ID为" + productId + "的商品在redis和数据库中都不存在！");
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
	public String getOrderFromRedis(Integer userId, Integer orderId) throws ValidateException {
		// TODO Auto-generated method stub
		if (userId == null) {
			throw new ValidateException("用户ID不能为空！");
		}
		if (orderId == null) {
			throw new ValidateException("订单ID不能为空！");
		}
		String orderJson = cacheRepository.hget(Constants.PREFIX_ORDERS_USER + userId,
				String.valueOf(orderId));
		if (StringUtil.isBlank(orderJson)) {
			OrderModel orderModel = new OrderModel();
			orderModel.setId(orderId);
			orderModel.setUserId(userId);
			orderModel = orderDao.getModel(orderModel);
			if (orderModel != null && orderModel.getId() != null) {
				cacheRepository.hset(Constants.PREFIX_ORDERS_USER + userId,
						String.valueOf(orderModel.getId()), orderModel.getDetail());
				orderJson = orderModel.getDetail();
			}
		}
		return orderJson;
	}

	@Override
	public void buildOrderToRedis(Integer userId, Integer orderId, String orderDetal)
			throws ValidateException {
		// TODO Auto-generated method stub
		if (userId == null) {
			throw new ValidateException("用户ID不能为空！");
		}
		if (orderId == null) {
			throw new ValidateException("订单ID不能为空！");
		}
		if (StringUtil.isBlank(orderDetal)) {
			throw new ValidateException("订单内容不能为空！");
		}
		cacheRepository.hset(Constants.PREFIX_ORDERS_USER + userId, String.valueOf(orderId),
				orderDetal);
		cacheRepository.lpush(Constants.PREFIX_ORDERS_UNFINISHED + userId, String.valueOf(orderId));
	}

	@Override
	public void reBuildOrderToRedis(Integer userId, Integer orderId) throws ValidateException {
		// TODO Auto-generated method stub
		if (userId == null) {
			throw new ValidateException("用户ID不能为空！");
		}
		if (orderId == null) {
			throw new ValidateException("订单ID不能为空！");
		}
		cacheRepository.lrem(Constants.PREFIX_ORDERS_UNFINISHED + userId, String.valueOf(orderId));
	}
}
