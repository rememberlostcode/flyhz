
package com.flyhz.shop.persistence;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.flyhz.framework.lang.CacheRepository;
import com.flyhz.framework.lang.RedisRepository;
import com.flyhz.framework.lang.SolrData;
import com.flyhz.framework.lang.ValidateException;
import com.flyhz.framework.util.Constants;
import com.flyhz.framework.util.JSONUtil;
import com.flyhz.framework.util.StringUtil;
import com.flyhz.shop.dto.BrandBuildDto;
import com.flyhz.shop.dto.BrandDto;
import com.flyhz.shop.dto.ProductDto;
import com.flyhz.shop.persistence.dao.OrderDao;
import com.flyhz.shop.persistence.dao.ProductDao;
import com.flyhz.shop.persistence.entity.OrderModel;
import com.flyhz.shop.persistence.entity.ProductModel;

@Service
public class RedisRepositoryImpl implements RedisRepository {
	private Logger			log	= LoggerFactory.getLogger(RedisRepositoryImpl.class);
	@Value(value = "${smile.solr.url}")
	private String			server_url;
	@Resource
	private CacheRepository	cacheRepository;
	@Resource
	private SolrData		solrData;
	@Resource
	private OrderDao		orderDao;
	@Resource
	private ProductDao		productDao;

	@Override
	public ProductDto getProductFromRedis(String productId) throws ValidateException {
		// TODO Auto-generated method stub
		if (StringUtil.isBlank(productId)) {
			throw new ValidateException(111111);
		}
		ProductDto productDto = new ProductDto();
		ProductModel productModel = productDao.getModelById(Integer.parseInt(productId));
		if (productModel != null && productModel.getBrandId() != null) {

			// 通过品牌ID获取品牌信息
			BrandBuildDto brandBuildDto = getBrandFromRedis(productModel.getBrandId());

			// 如果品牌不为空，把商品信息和品牌信息放入productDto
			if (brandBuildDto != null) {
				productDto.setId(productModel.getId());
				productDto.setName(productModel.getName());
				productDto.setPurchasingPrice(productModel.getPurchasingprice());

				if (productModel.getImgs() != null && !"".equals(productModel.getImgs())) {
					String[] p = JSONUtil.getJson2Entity(productModel.getImgs(), String[].class);
					productDto.setImgs(p);
				}

				BrandDto brand = new BrandDto();
				brand.setId(brandBuildDto.getId());
				brand.setName(brandBuildDto.getName());
				productDto.setBrand(brand);
			}
		} else {
			log.warn("ID为" + productId + "的商品在redis和数据库中都不存在！");
			return null;
		}
		return productDto;
	}

	/**
	 * 通过品牌ID获取品牌信息
	 * 
	 * @param bid
	 * @return
	 */
	private BrandBuildDto getBrandFromRedis(Integer bid) {
		String brandJson = cacheRepository.hget(Constants.REDIS_KEY_BRANDS, String.valueOf(bid));
		return JSONUtil.getJson2Entity(brandJson, BrandBuildDto.class);
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
		if (StringUtil.isBlank(orderJson)) {// redis里没有找到就从数据库找
			OrderModel orderModel = new OrderModel();
			orderModel.setId(orderId);
			orderModel.setUserId(userId);
			orderModel = orderDao.getModel(orderModel);
			if (orderModel != null && orderModel.getId() != null) {// 数据库找到了，在build到redis
				cacheRepository.hset(Constants.PREFIX_ORDERS_USER + userId,
						String.valueOf(orderModel.getId()), orderModel.getDetail());
				orderJson = orderModel.getDetail();// 设置返回结果
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
		// build到redis
		cacheRepository.hset(Constants.PREFIX_ORDERS_USER + userId, String.valueOf(orderId),
				orderDetal);
		// solr建立订单索引
		solrData.submitOrder(userId, orderId, null, null);
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
		// solr修改订单索引
		solrData.submitOrder(userId, orderId, "1", null);
	}
}
