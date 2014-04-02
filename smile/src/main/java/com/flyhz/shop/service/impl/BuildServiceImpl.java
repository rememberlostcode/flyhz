
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.flyhz.framework.lang.CacheRepository;
import com.flyhz.framework.util.Constants;
import com.flyhz.framework.util.JSONUtil;
import com.flyhz.framework.util.UrlUtil;
import com.flyhz.shop.build.solr.Fraction;
import com.flyhz.shop.build.solr.PageModel;
import com.flyhz.shop.build.solr.ProductFraction;
import com.flyhz.shop.build.solr.SolrServer;
import com.flyhz.shop.dto.ProductBuildDto;
import com.flyhz.shop.persistence.dao.BrandDao;
import com.flyhz.shop.persistence.dao.CategoryDao;
import com.flyhz.shop.persistence.dao.ProductDao;
import com.flyhz.shop.persistence.entity.BrandModel;
import com.flyhz.shop.persistence.entity.CategoryModel;
import com.flyhz.shop.service.BuildService;

@Service
public class BuildServiceImpl implements BuildService {

	@Resource
	private ProductDao		productDao;
	@Resource
	private CategoryDao		categoryDao;
	@Resource
	private BrandDao		brandDao;
	@Resource
	@Value(value = "${smile.solr.url}")
	public String			server_url;
	@Resource
	private CacheRepository	cacheRepository;
	@Resource
	private ProductFraction	productFraction;

	@Override
	public void buildData() {
		System.out.println("buildData开始...");
		try {
			buildSolr();
			buildRedis();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("buildData结束");
	}

	public void buildSolr() {
		System.out.println("buildSolr开始...");
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
				doc.addField("be", product.getBe());

				doc.addField("cid", product.getCid());
				doc.addField("ce", product.getCe());

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
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("buildSolr结束");
	}

	public void buildRedis() {
		System.out.println("buildRedis开始...");

		/******** build商品详情 start *******/
		int resultSize = 500;// 500条数据查询一次数据库
		int thisNum = 0;
		int maxIdCount = getCountOfAll();
		List<ProductBuildDto> productList = null;
		while (thisNum < maxIdCount) {
			productList = findAll(thisNum, resultSize);
			for (int i = 0; i < productList.size(); i++) {
				if (productList.get(i).getId() != null)
					cacheRepository.set(String.valueOf(productList.get(i).getId()),
							productList.get(i));
			}
			// 设置新的分页查询参数
			thisNum += resultSize;
		}
		/******** build商品详情 end *******/

		/******** build商品品牌分类 start *******/
		List<CategoryModel> catList = categoryDao.getModelList();
		List<BrandModel> brandList = brandDao.getModelList();

		// build所有分类
		cacheRepository.set(Constants.PREFIX_CATS, "all", JSONUtil.getEntity2Json(catList));
		// build所有品牌
		cacheRepository.set(Constants.PREFIX_BRANDS, "all", JSONUtil.getEntity2Json(brandList));

		// 每个品牌的推荐商品
		for (int j = 0; j < brandList.size(); j++) {
			cacheRepository.set(Constants.PREFIX_BRANDS_RECOMMEND,
					String.valueOf(brandList.get(j).getId()),
					getProductsStringBySolr(null, brandList.get(j).getId()));
		}
		// build各品牌各分类推荐商品
		for (int i = 0; i < catList.size(); i++) {
			for (int j = 0; j < brandList.size(); j++) {
				String allCatesAndBrands = getProductsStringBySolr(catList.get(i).getId(),
						brandList.get(j).getId());
				System.out.println(catList.get(i).getName() + "&" + brandList.get(j).getName()
						+ "=" + allCatesAndBrands);
				cacheRepository.set(Constants.PREFIX_BRANDS_CATS, catList.get(i).getId() + "_"
						+ brandList.get(j).getId(), allCatesAndBrands);
			}
		}
		System.out.println(cacheRepository.getString(Constants.PREFIX_BRANDS_CATS, "1_2"));
		/******** build商品品牌分类 end *******/
		System.out.println("buildRedis结束");
	}

	public int getCountOfAll() {
		return productDao.getCountOfAll();
	}

	public List<ProductBuildDto> findAll(int start, int num) {
		PageModel page = new PageModel();
		page.setStart(start);
		page.setNum(num);
		return productDao.findAll(page);
	}

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
		String rStr = "";
		HashMap<String, String> param = getBaseParams();
		rStr = getSolrResult(param);

		if (rStr != null && !"".equals(rStr)) {
			rStr = getDocsString(rStr);
			list = JSONUtil.getJson2EntityList(rStr, List.class, ProductBuildDto.class);
		}
		return list;
	}

	/**
	 * 从solr获得对应品牌和分类的商品
	 * 
	 * @param cid
	 *            分类ID
	 * @param bid
	 *            品牌ID
	 * @return json格式字符串
	 */
	private String getProductsStringBySolr(Integer cid, Integer bid) {
		String rStr = "";
		if (bid == null)
			return rStr;
		HashMap<String, String> param = getBaseParams();
		String t = "bid%3A" + bid;
		if (cid != null)
			t += "+AND+cid%3A" + cid;
		param.put("q", t);
		rStr = getSolrResult(param);

		if (rStr != null && !"".equals(rStr)) {
			rStr = getDocsString(rStr);
		}
		return rStr;
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
	 * 从solr取得结果
	 * 
	 * @param param
	 * @return
	 */
	private String getSolrResult(HashMap<String, String> param) {
		return UrlUtil.getStringByGetNotEncod(server_url + Constants.SEARCH_URL, param);
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
