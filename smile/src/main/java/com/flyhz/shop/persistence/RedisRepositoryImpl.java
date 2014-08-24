
package com.flyhz.shop.persistence;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.flyhz.framework.lang.CacheRepository;
import com.flyhz.framework.lang.RedisRepository;
import com.flyhz.framework.lang.SolrData;
import com.flyhz.framework.lang.ValidateException;
import com.flyhz.framework.util.Constants;
import com.flyhz.framework.util.JSONUtil;
import com.flyhz.framework.util.StringUtil;
import com.flyhz.shop.build.solr.SolrPage;
import com.flyhz.shop.dto.BrandBuildDto;
import com.flyhz.shop.dto.BrandDto;
import com.flyhz.shop.dto.OrderDto;
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
	private SolrData		solrData;
	@Resource
	private OrderDao		orderDao;
	@Resource
	private ProductDao		productDao;

	@Override
	public ProductDto getProductFromRedis(String productId) throws ValidateException {
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
				productDto.setBrandstyle(productModel.getBrandstyle());
				productDto.setColor(productModel.getColor());
				productDto.setCurrency(productModel.getCurrency());
				productDto.setSymbol(productModel.getSymbol());

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
			solrData.removeProduct(productId);
			log.warn("ID为" + productId + "的商品在数据库中都不存在，将商品从solr中删除！");
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
		if (userId == null) {
			throw new ValidateException(130002);
		}
		if (orderId == null) {
			throw new ValidateException(130003);
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
	public void buildOrderToRedis(Integer userId, Integer orderId, String status, Date gmtModify,
			String orderDetal) throws ValidateException {
		if (userId == null) {
			throw new ValidateException(130002);
		}
		if (orderId == null) {
			throw new ValidateException(130003);
		}
		if (StringUtil.isBlank(orderDetal)) {
			throw new ValidateException(130004);
		}
		// build到redis
		cacheRepository.hset(Constants.PREFIX_ORDERS_USER + userId, String.valueOf(orderId),
				orderDetal);
		// solr建立订单索引
		solrData.submitOrder(null,userId, orderId, status, gmtModify, null);
	}

	@Override
	public void reBuildOrderToRedis(Long tid,Integer userId, Integer orderId, String status)
			throws ValidateException {
		if (userId == null) {
			throw new ValidateException(130002);
		}
		if (orderId == null) {
			throw new ValidateException(130003);
		}
		// solr修改订单索引
		solrData.submitOrder(tid,userId, orderId, status, null, null);
	}

	public void chacheOrders() {
		int mysqlSize = 500;// 每次取500条
		SolrPage solrPage = new SolrPage();
		// 500条数据查询一次并插入数据库
		int orderStart = 0;

		int maxOrderCount = orderDao.getAllOrdersCount(solrPage);
		List<OrderModel> detailList = null;
		OrderDto orderDto = null;
		solrPage.setNum(mysqlSize);

		while (orderStart < maxOrderCount) {
			solrPage.setStart(orderStart);
			detailList = orderDao.findAllOrders(solrPage);
			for (int i = 0; i < detailList.size(); i++) {
				if (detailList.get(i) != null && detailList.get(i).getDetail() != null
						&& detailList.get(i).getId() != null) {
					try {
						// 转换后获取商品购买数量
						orderDto = JSONUtil.getJson2Entity(detailList.get(i).getDetail(),
								OrderDto.class);
						if (orderDto != null && orderDto.getUser() != null) {
							orderDto.setId(detailList.get(i).getId());
							String userId = String.valueOf(orderDto.getUser().getId());
							cacheRepository.hset(Constants.PREFIX_ORDERS_USER + userId,
									String.valueOf(orderDto.getId()),
									JSONUtil.getEntity2Json(orderDto));
							solrData.submitOrder(null,orderDto.getUser().getId(), orderDto.getId(),
									detailList.get(i).getStatus(), null, null);
						}
					} catch (Exception e) {
						e.printStackTrace();
						log.error(e.getMessage());
					}

				}
			}

			// 设置新的分页查询参数
			orderStart += mysqlSize;
		}
	}
}
