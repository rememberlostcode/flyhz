
package com.flyhz.shop.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.flyhz.framework.lang.CacheRepository;
import com.flyhz.framework.util.Constants;
import com.flyhz.framework.util.JSONUtil;
import com.flyhz.framework.util.StringUtil;
import com.flyhz.framework.util.UrlUtil;
import com.flyhz.shop.build.solr.Fraction;
import com.flyhz.shop.build.solr.ProductFraction;
import com.flyhz.shop.build.solr.SolrPage;
import com.flyhz.shop.build.solr.SolrServer;
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
	private Logger			log	= LoggerFactory.getLogger(BuildServiceImpl.class);
	@Resource
	private ProductDao		productDao;
	@Resource
	private CategoryDao		categoryDao;
	@Resource
	private BrandDao		brandDao;
	@Resource
	private OrderDao		orderDao;
	@Resource
	@Value(value = "${smile.solr.url}")
	private String			server_url;
	@Resource
	private CacheRepository	cacheRepository;
	@Resource
	private ProductFraction	productFraction;
	@Resource
	private OrderService	orderService;

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
		// 500条数据查询一次并插入数据库
		int resultSize = 500;
		int thisNum = 0;

		int maxIdCount = getCountOfAll();
		List<ProductBuildDto> productList = null;

		HttpSolrServer solrServer = null;
		Fraction fraction = new Fraction();
		Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		while (thisNum < maxIdCount) {

			productList = findAll(thisNum, resultSize);
			ProductBuildDto product = null;
			SolrInputDocument doc = null;

			for (int i = 0; i < productList.size(); i++) {
				product = productList.get(i);
				doc = new SolrInputDocument();
				doc.addField("id", product.getId());
				doc.addField("n", product.getN());
				doc.addField("d", product.getD());
				doc.addField("lp", product.getLp());
				doc.addField("pp", product.getPp());
				if (product.getLp() != null && product.getPp() != null)
					doc.addField("sp", product.getLp().subtract(product.getPp()));
				doc.addField("p", product.getP());
				doc.addField("t", product.getT());

				doc.addField("bid", product.getBid());
				// doc.addField("be", product.getBe());

				doc.addField("cid", product.getCid());
				// doc.addField("ce", product.getCe());

				fraction.setProductId(product.getId());
				fraction.setLastUpadteTime(product.getT());
				doc.addField("sf", productFraction.getProductFraction(fraction));// 分数排序
				doc.addField("st", i);// 时间排序
				docs.add(doc);
			}

			// 设置新的分页查询参数
			thisNum += resultSize;

			// 提交到solr
			solrServer = SolrServer.getServer();
			try {
				solrServer.add(docs);
				solrServer.commit();
			} catch (SolrServerException e) {
				log.error(e.getMessage());
			} catch (IOException e) {
				log.error(e.getMessage());
			} finally {
				solrServer.shutdown();
			}
		}
		log.info("buildSolr结束");
	}

	public void buildRedis() {
		// TODO Auto-generated method stub
		log.info("buildRedis开始...");

		/******** build商品详情 start *******/
		int resultSize = 500;// 500条数据查询一次数据库
		int thisNum = 0;
		int maxIdCount = getCountOfAll();
		List<ProductBuildDto> productList = null;
		while (thisNum < maxIdCount) {
			productList = findAll(thisNum, resultSize);
			for (int i = 0; i < productList.size(); i++) {
				if (productList.get(i) != null && productList.get(i).getId() != null
						|| StringUtil.isNotBlank(productList.get(i).getBs())) {
					cacheRepository.hset(Constants.REDIS_KEY_PRODUCTS, productList.get(i).getId()
																					.toString(),
							JSONUtil.getEntity2Json(productList.get(i)));
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

		/******** build推荐商品 start *******/
		cacheRepository.setString(Constants.REDIS_KEY_RECOMMEND_INDEX,
				getProductsStringBySolr(null, null));
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
		for (int i = 0; i < brandList.size(); i++) {
			if (brandList.get(i) != null && brandList.get(i).getId() != null) {
				cacheRepository.setString(
						Constants.PREFIX_BRANDS_RECOMMEND
								+ String.valueOf(brandList.get(i).getId()),
						getProductsStringBySolr(null, brandList.get(i).getId()));
			}
		}
		// build各品牌各分类推荐商品（前10个）
		for (int i = 0; i < catList.size(); i++) {
			for (int j = 0; j < brandList.size(); j++) {
				if (catList.get(i) != null && catList.get(i).getId() != null
						&& brandList.get(j) != null && brandList.get(j).getId() != null) {
					String allCatesAndBrands = getProductsStringBySolr(catList.get(i).getId(),
							brandList.get(j).getId());
					System.out.println(catList.get(i).getName() + "&" + brandList.get(j).getName()
							+ "=" + allCatesAndBrands);
					cacheRepository.setString(Constants.PREFIX_BRANDS_CATES
							+ catList.get(i).getId() + "_" + brandList.get(j).getId(),
							allCatesAndBrands);
				}
			}
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

	/**
	 * 获取商品总数
	 * 
	 * @return
	 */
	private int getCountOfAll() {
		return productDao.getCountOfAll();
	}

	/**
	 * 分页获取商品
	 * 
	 * @param start
	 *            开始序号
	 * @param num
	 *            取得条数
	 * @return
	 */
	private List<ProductBuildDto> findAll(int start, int num) {
		SolrPage page = new SolrPage();
		page.setStart(start);
		page.setNum(num);
		return productDao.findAll(page);
	}

	// /**
	// * Build所有订单
	// */
	// private void buildOrders() {
	// List<OrderModel> orderList = orderDao.getModelList();
	// for (int i = 0; i < orderList.size(); i++) {
	// buildOrder(orderList.get(i));
	// }
	// }

	/**
	 * 从solr获得对应品牌和分类的商品
	 * 
	 * @param cid
	 *            分类ID
	 * @param bid
	 *            品牌ID
	 * @return
	 */
	@SuppressWarnings("unused")
	private List<ProductBuildDto> getProductsListBySolr(Integer cid, Integer bid) {
		List<ProductBuildDto> list = new ArrayList<ProductBuildDto>();
		String resultStr = "";
		HashMap<String, String> param = getBaseParams();
		resultStr = getSolrResult(param);

		if (resultStr != null && !"".equals(resultStr)) {
			resultStr = getDocsString(resultStr);
			list = JSONUtil.getJson2EntityList(resultStr, List.class, ProductBuildDto.class);
		}
		return list;
	}

	/**
	 * 从solr获得指定品牌和分类的商品
	 * 
	 * @param cid
	 *            分类ID
	 * @param bid
	 *            品牌ID
	 * @return json格式字符串
	 */
	private String getProductsStringBySolr(Integer cid, Integer bid) {
		String rStr = "";
		String t = "*%3A*";
		HashMap<String, String> param = getBaseParams();

		if (bid == null && cid == null) {
		} else if (bid != null && cid == null) {
			t = "bid%3A" + bid;
		} else if (cid != null && bid == null) {
			t = "cid%3A" + cid;
		} else {
			t = "bid%3A" + bid + "+AND+cid%3A" + cid;
		}
		param.put("q", t);
		rStr = getSolrResult(param);

		if (rStr != null && !"".equals(rStr)) {
			rStr = getDocsString(rStr);
		}
		return rStr;
	}

	/**
	 * 从solr取得结果
	 * 
	 * @param param
	 * @return
	 */
	private String getSolrResult(HashMap<String, String> param) {
		// HttpSolrServer solrServer = SolrServer.getServer();
		// // 创建查询参数以及设定的查询参数
		// SolrQuery params = new SolrQuery();
		// params.set("q", "*:*");
		//
		// // 查询并获取相应的结果！
		// QueryResponse response = null;
		// try {
		// response = solrServer.query(params);
		// } catch (SolrServerException e) {
		// System.err.println(e.getMessage());
		// e.printStackTrace();
		// } catch (Exception e) {
		// System.err.println(e.getMessage());
		// e.printStackTrace();
		// } finally {
		// solrServer.shutdown();
		// }
		//
		// // 获取相关的查询结果
		// if (response != null) {
		// System.out.println("查询耗时（ms）：" + response.getQTime());
		// SolrDocumentList list = response.getResults();
		// System.out.println(response.toString());
		// }
		return UrlUtil.getStringByGetNotEncod(server_url + Constants.SEARCH_URL, param);
	}

	/**
	 * 获得slor基础参数
	 * 
	 * @return
	 */
	private HashMap<String, String> getBaseParams() {
		HashMap<String, String> param = new HashMap<String, String>();
		param.put("sort", "sf+desc");
		param.put("rows", "10");
		return param;
	}

	/**
	 * 去除不必要的字符，只需要docs
	 * 
	 * @param str
	 * @return
	 */
	private String getDocsString(String str) {
		int index = str.indexOf("\"docs\":[");
		return str.substring(index + 7, str.length() - 2);
	}
}
