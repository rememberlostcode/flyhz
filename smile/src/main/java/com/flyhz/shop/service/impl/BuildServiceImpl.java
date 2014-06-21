
package com.flyhz.shop.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.flyhz.framework.lang.CacheRepository;
import com.flyhz.framework.lang.RedisRepository;
import com.flyhz.framework.lang.SolrData;
import com.flyhz.framework.util.Constants;
import com.flyhz.framework.util.JSONUtil;
import com.flyhz.framework.util.StringUtil;
import com.flyhz.framework.util.UrlUtil;
import com.flyhz.shop.build.solr.ProductFraction;
import com.flyhz.shop.build.solr.SolrPage;
import com.flyhz.shop.dto.BrandBuildDto;
import com.flyhz.shop.dto.CategoryBuildDto;
import com.flyhz.shop.dto.ProductBuildDto;
import com.flyhz.shop.persistence.dao.ActivityDao;
import com.flyhz.shop.persistence.dao.BrandDao;
import com.flyhz.shop.persistence.dao.CategoryDao;
import com.flyhz.shop.persistence.dao.ProductDao;
import com.flyhz.shop.persistence.entity.ActivityModel;
import com.flyhz.shop.service.BuildService;

@Service
public class BuildServiceImpl implements BuildService {
	private Logger			log			= LoggerFactory.getLogger(BuildServiceImpl.class);
	@Resource
	private ProductDao		productDao;
	@Resource
	private CategoryDao		categoryDao;
	@Resource
	private BrandDao		brandDao;
	@Resource
	private CacheRepository	cacheRepository;
	@Resource
	private ProductFraction	productFraction;
	@Resource
	private SolrData		solrData;
	@Resource
	private RedisRepository	redisRepository;
	@Resource
	private ActivityDao 	activityDao;
	/**
	 * 500条数据查询一次并插入数据库
	 */
	private final int		mysqlSize	= 100;

	public void buildData() {
		// TODO Auto-generated method stub
		log.info("buildData开始（包括solr和redis）...");
		try {
			buildSolr();
			buildRedis();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		log.info("buildData结束");
	}

	public void buildSolr() {
		// TODO Auto-generated method stub
		log.info("buildSolr开始...");
		
		log.info("清除所有商品索引...");
		solrData.cleanProduct();
		log.info("清除所有商品索引完成");

		log.info("建立所有商品索引...");
		SolrPage solrPage = new SolrPage();
		solrPage.setNum(mysqlSize);
		int thisNum = 0;
		int maxIdCount = productDao.getCountOfAll();
		List<ProductBuildDto> productList = null;

		while (thisNum < maxIdCount) {
			solrPage.setStart(thisNum);
			productList = productDao.findAll(solrPage);
			solrData.submitProductList(productList);
			// 设置新的分页查询参数
			thisNum += mysqlSize;
		}
		log.info("建立所有商品索引完成");

		// solr建立订单索引
		log.info("重建订单索引...");
		solrData.reBuildOrder();
		log.info("重建订单索引完成");

		log.info("buildSolr结束");
	}

	public void buildRedis() {
		// TODO Auto-generated method stub
		log.info("buildRedis开始...");

		/******** build首页活动 start *******/
		cacheRepository.setString(Constants.REDIS_KEY_RECOMMEND_INDEX,
				JSONUtil.getEntity2Json(activityDao.getModelList(new ActivityModel())));
		/******** build首页活动 end *******/

		/******** build商品品牌分类 start *******/
		List<CategoryBuildDto> catList = categoryDao.getCategoryBuildDtoList();
		List<BrandBuildDto> brandList = brandDao.getBrandBuildDtoList();

		// build所有分类
		for (int i = 0; i < catList.size(); i++) {
			if (catList.get(i) != null && catList.get(i).getId() != null) {
				cacheRepository.hset(Constants.REDIS_KEY_CATES, catList.get(i).getId().toString(),
						JSONUtil.getEntity2Json(catList.get(i)));
			} else {
				log.warn("分类ID为空！");
			}
		}
		// build所有品牌
		for (int i = 0; i < brandList.size(); i++) {
			if (brandList.get(i) != null && brandList.get(i).getId() != null) {
				cacheRepository.hset(Constants.REDIS_KEY_BRANDS, brandList.get(i).getId()
																			.toString(),
						JSONUtil.getEntity2Json(brandList.get(i)));
			} else {
				log.warn("品牌ID为空！");
			}
		}

		// build每个品牌的推荐商品（前10个）（首页不选分类时）
		StringBuilder brandProducts = new StringBuilder(100);
		String productsJsonString = "";
		brandProducts.append("[");
		for (int i = 0; i < brandList.size(); i++) {
			if (brandList.get(i) != null && brandList.get(i).getId() != null) {
				productsJsonString = solrData.getProductsString(null, brandList.get(i).getId());
				if (StringUtil.isNotBlank(productsJsonString) && !"[]".equals(productsJsonString)) {// 该品牌没有商品就跳过
					if (brandProducts.length() > 1) {
						brandProducts.append(",");
					}
					brandProducts.append(getBrandAndProductsJsonString(brandList.get(i),productsJsonString));
				}
			}
		}
		brandProducts.append("]");
		cacheRepository.setString(Constants.REDIS_KEY_BRANDS_RECOMMEND, brandProducts.toString());

		// build各品牌各分类推荐商品（前10个）（首页选分类时）
		StringBuilder brandCateProducts = new StringBuilder(100);
		for (int i = 0; i < catList.size(); i++) {
			brandCateProducts.append("[");
			for (int j = 0; j < brandList.size(); j++) {
				if (catList.get(i) != null && catList.get(i).getId() != null
						&& brandList.get(j) != null && brandList.get(j).getId() != null) {

					productsJsonString = solrData.getProductsString(catList.get(i).getId(), brandList.get(j)
																						.getId());
					if (StringUtil.isNotBlank(productsJsonString) && !"[]".equals(productsJsonString)) {// 该品牌没有商品就跳过
						if (brandCateProducts.length() > 1) {
							brandCateProducts.append(",");
						}
						brandCateProducts.append(getBrandAndProductsJsonString(brandList.get(j),productsJsonString));
					}
				}
			}
			brandCateProducts.append("]");
			cacheRepository.setString(Constants.PREFIX_BRANDS_RECOMMEND_CATES
					+ catList.get(i).getId(), brandCateProducts.toString());
			
			//再次使用前先清空brandCateProducts
			brandCateProducts.delete(0, brandCateProducts.length());
		}

		/******** build商品品牌分类 end *******/

		/******** build用户订单 start *******/
		redisRepository.chacheOrders();
		/******** build用户订单 end *******/
		log.info("buildRedis结束");
	}
	
	public double getDollarExchangeRate() {
		// TODO Auto-generated method stub
		log.info("开始获取美元汇率...");
		double dollarExchangeRate = 6.5;
		String response = UrlUtil.sendGet("http://download.finance.yahoo.com/d/quotes.csv?e=.csv&f=sl1d1t1&s=USDCNY=x");
		if (StringUtil.isNotBlank(response)) {
			String[] responses = response.split(",");
			for (int i = 0; i < responses.length; i++) {
				log.info(responses[i]);
			}
			try {
				dollarExchangeRate = Double.parseDouble(responses[1]);
				log.info("当前美元汇率为" + dollarExchangeRate);
			} catch (Exception e) {
				log.error("获取美元汇率失败！！！" + e.getLocalizedMessage());
			}
		}
		log.info("获取美元汇率结束");
		return dollarExchangeRate;
	}
	
	/**
	 * 获取品牌和该品牌下商品的json格式字符串，包括最外层的{}
	 * @param brand
	 * @return
	 */
	private String getBrandAndProductsJsonString(BrandBuildDto brand,String productsJsonString){
		StringBuilder json = new StringBuilder(100);
		json.append("{");
		
		json.append("\"brand\":");
		json.append(JSONUtil.getEntity2Json(brand));
		
		json.append(",\"gs\":");
		json.append(productsJsonString);
		
		json.append("}");
		return json.toString();
	}

}