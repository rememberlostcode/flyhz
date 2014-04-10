
package com.flyhz.shop.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.flyhz.framework.lang.CacheRepository;
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
import com.flyhz.shop.persistence.dao.BrandDao;
import com.flyhz.shop.persistence.dao.CategoryDao;
import com.flyhz.shop.persistence.dao.OrderDao;
import com.flyhz.shop.persistence.dao.ProductDao;
import com.flyhz.shop.service.BuildService;
import com.flyhz.shop.service.OrderService;

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
	private OrderDao		orderDao;
	@Resource
	private CacheRepository	cacheRepository;
	@Resource
	private ProductFraction	productFraction;
	@Resource
	private OrderService	orderService;
	@Resource
	private SolrData		solrData;
	/**
	 * 500条数据查询一次并插入数据库
	 */
	private final int		mysqlSize	= 500;

	public void buildData() {
		// TODO Auto-generated method stub
		log.info("buildData开始...");
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

		// solr建立订单索引
		solrData.reBuildOrder();

		log.info("buildSolr结束");
	}

	public void buildRedis() {
		// TODO Auto-generated method stub
		log.info("buildRedis开始...");

		/******** build商品详情 start *******/
		int resultSize = 500;// 500条数据查询一次数据库
		int thisNum = 0;
		SolrPage solrPage = new SolrPage();
		solrPage.setNum(mysqlSize);
		int maxIdCount = productDao.getCountOfAll();
		List<ProductBuildDto> productList = null;
		while (thisNum < maxIdCount) {
			solrPage.setStart(thisNum);
			productList = productDao.findAll(solrPage);
			for (int i = 0; i < productList.size(); i++) {
				if (productList.get(i) != null && productList.get(i).getId() != null
						|| StringUtil.isNotBlank(productList.get(i).getBs())) {
					cacheRepository.hset(Constants.REDIS_KEY_PRODUCTS, productList.get(i).getId()
																					.toString(),
							JSONUtil.getEntity2Json(productList.get(i)).replace("\"[", "[")
									.replace("]\"", "]").replace("\\", ""));
					cacheRepository.hset(Constants.REDIS_KEY_PRODUCT_CN,
							productList.get(i).getBs(), productList.get(i).getId().toString());
				} else {
					if (productList.get(i) == null) {
						log.warn("build商品时，商品为空！");
					} else if (productList.get(i).getId() == null) {
						log.warn("build商品时，商品ID为空！");
					} else if (StringUtil.isNotBlank(productList.get(i).getBs())) {
						log.warn("build商品时，商品款号为空！");
					}
				}
			}
			// 设置新的分页查询参数
			thisNum += resultSize;
		}
		/******** build商品详情 end *******/

		/******** build订单 start *******/
		// buildOrders();
		/******** build订单 end *******/

		/******** build首页推荐商品 start *******/
		cacheRepository.setString(Constants.REDIS_KEY_RECOMMEND_INDEX,
				solrData.getProductsString(null, null));
		/******** build商品详情 end *******/

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

		// build每个品牌的推荐商品（前10个）
		StringBuilder brandProducts = new StringBuilder(100);
		brandProducts.append("[");
		for (int i = 0; i < brandList.size(); i++) {
			if (brandList.get(i) != null && brandList.get(i).getId() != null) {
				// cacheRepository.setString(
				// Constants.PREFIX_BRANDS_RECOMMEND
				// + String.valueOf(brandList.get(i).getId()),
				// solrData.getProductsString(null, brandList.get(i).getId()));
				if (brandProducts.length() > 1) {
					brandProducts.append(",");
				}
				brandProducts.append("{\"id\":\"" + brandList.get(i).getId() + "\",");
				brandProducts.append("\"n\":\"" + brandList.get(i).getName() + "\",");
				brandProducts.append("\"gs\":");
				brandProducts.append(solrData.getProductsString(null, brandList.get(i).getId()));
				brandProducts.append("}");
			}
		}
		brandProducts.append("]");
		cacheRepository.setString(Constants.REDIS_KEY_BRANDS_RECOMMEND, brandProducts.toString());

		// build各品牌各分类推荐商品（前10个）
		StringBuilder brandCateProducts = new StringBuilder(100);
		for (int i = 0; i < catList.size(); i++) {
			brandCateProducts.append("[");
			for (int j = 0; j < brandList.size(); j++) {
				if (catList.get(i) != null && catList.get(i).getId() != null
						&& brandList.get(j) != null && brandList.get(j).getId() != null) {

					if (brandCateProducts.length() > 1) {
						brandCateProducts.append(",");
					}
					brandCateProducts.append("{\"i\":\"" + brandList.get(j).getId() + "\",");
					brandCateProducts.append("\"n\":\"" + brandList.get(j).getName() + "\",");
					brandCateProducts.append("\"gs\":");
					brandCateProducts.append(solrData.getProductsString(catList.get(i).getId(),
							brandList.get(j).getId()));
					brandCateProducts.append("}");
				}
			}
			brandCateProducts.append("]");
			cacheRepository.setString(Constants.PREFIX_BRANDS_RECOMMEND_CATES
					+ catList.get(i).getId(), brandCateProducts.toString());
			brandCateProducts.delete(0, brandCateProducts.length());
		}

		/******** build商品品牌分类 end *******/
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

}