
package com.flyhz.shop.build.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.flyhz.framework.lang.CacheRepository;
import com.flyhz.framework.lang.RedisRepository;
import com.flyhz.framework.lang.SolrData;
import com.flyhz.framework.util.DateUtil;
import com.flyhz.framework.util.StringUtil;
import com.flyhz.framework.util.UrlUtil;
import com.flyhz.shop.dto.ProductBuildDto;

@Service
public class SolrDataImpl implements SolrData {
	public static final String		PRODUCT_URL		= "/solr/smile_product";
	public static final String		ORDER_URL		= "/solr/smile_order";
	public static final String		SEARCH_URL		= "/select";

	Logger							log				= LoggerFactory.getLogger(getClass());
	@Resource
	@Value(value = "${smile.solr.url}")
	private String					solr_url;
	@Resource
	private ProductFraction			productFraction;
	@Resource
	private CacheRepository			cacheRepository;
	@Resource
	private RedisRepository			redisRepository;
	private static HttpSolrServer	productServer	= null;
	private static HttpSolrServer	orderServer		= null;

	public HttpSolrServer getServer(String url) {
		if (PRODUCT_URL.equals(url)) {
			if (productServer == null) {
				productServer = new HttpSolrServer(solr_url + url);
				productServer.setSoTimeout(1000); // socket read timeout
				productServer.setConnectionTimeout(1000);
				productServer.setDefaultMaxConnectionsPerHost(100);
				productServer.setMaxTotalConnections(100);
				productServer.setFollowRedirects(false); // defaults to false
				productServer.setAllowCompression(true);
				productServer.setMaxRetries(1);
			}
			return productServer;
		} else if (ORDER_URL.equals(url)) {
			if (orderServer == null) {
				orderServer = new HttpSolrServer(solr_url + url);
				orderServer.setSoTimeout(1000); // socket read timeout
				orderServer.setConnectionTimeout(1000);
				orderServer.setDefaultMaxConnectionsPerHost(100);
				orderServer.setMaxTotalConnections(100);
				orderServer.setFollowRedirects(false); // defaults to false
				orderServer.setAllowCompression(true);
				orderServer.setMaxRetries(1);
			}
			return orderServer;
		} else {
			return null;
		}
	}

	public void reBuildOrder() {
		UrlUtil.sendGet(solr_url + ORDER_URL + "/dataimport?full-import&commit=y&clean=y");
	}

	public void submitProduct(ProductBuildDto productBuildDto) {

	}

	public void submitProductList(List<ProductBuildDto> productList) {
		Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();

		ProductBuildDto product = null;
		SolrInputDocument doc = null;

		String[] pictures = null;
		for (int i = 0; i < productList.size(); i++) {
			product = productList.get(i);
			doc = new SolrInputDocument();
			doc.addField("id", product.getId());// ID
			doc.addField("n", product.getN());// 名称
			doc.addField("d", product.getD());// 说明
			doc.addField("bs", product.getBs());// 款号
			doc.addField("lp", product.getLp());// 本地价格
			doc.addField("pp", product.getPp());// 代购价格

			// 计算差价（即折扣）
			if (product.getLp() != null && product.getPp() != null) {
				doc.addField("sp", product.getLp().subtract(product.getPp()));
			} else {
				doc.addField("sp", 0);
			}

			if (594 == product.getId()) {
				System.out.println(product);
			}
			// 原图封面
			if (product.getImgs() != null) {
				pictures = product.getImgs().replace("[", "").replace("]", "").replace("\"", "")
									.split(",");
				for (int k = 0; k < pictures.length; k++) {
					doc.addField("imgs", pictures[k]);
				}
			}
			// 大图封面
			if (product.getBp() != null) {
				pictures = product.getBp().replace("[", "").replace("]", "").replace("\"", "")
									.split(",");
				for (int k = 0; k < pictures.length; k++) {
					doc.addField("bp", pictures[k]);
				}
			}
			// 小图封面
			if (product.getP() != null) {
				pictures = product.getP().replace("[", "").replace("]", "").replace("\"", "")
									.split(",");
				for (int k = 0; k < pictures.length; k++) {
					doc.addField("p", pictures[k]);
				}
			}

			doc.addField("t", DateUtil.strToDateLong(product.getT()));// 时间
			doc.addField("bid", product.getBid());// 品牌ID
			doc.addField("be", product.getBe());// 品牌名称
			doc.addField("cid", product.getCid());// 分类ID
			doc.addField("ce", product.getCe());// 分类名称

			doc.addField("sf", productFraction.getProductFraction(product));// 分数
			doc.addField("st", product.getSt());// 时间排序值
			doc.addField("sd", product.getSd());// 折扣排序值
			doc.addField("ss", product.getSs());// 销售量排序值
			doc.addField("sn", product.getSn());// 销售量
			doc.addField("zsn", product.getZsn());// 一周销售量
			docs.add(doc);
		}

		// 提交到solr
		HttpSolrServer solrServer = getServer(PRODUCT_URL);
		try {
			solrServer.add(docs);
			solrServer.commit();
		} catch (SolrServerException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		} finally {
			// solrServer.shutdown();
		}
	}

	public void submitOrder(Integer userId, Integer orderId, String status, Date gmtModify) {
		if (StringUtil.isBlank(status)) {
			status = "0";
		}
		if (gmtModify == null) {
			gmtModify = new Date();
		}
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", orderId);
		doc.addField("user_id", userId);
		doc.addField("status", status);
		doc.addField("gmt_modify", gmtModify);

		// 提交到solr
		HttpSolrServer solrServer = getServer(ORDER_URL);
		try {
			solrServer.add(doc);
			solrServer.commit();
		} catch (SolrServerException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		} finally {
			// solrServer.shutdown();
		}
	}

	public String getProductsString(Integer cid, Integer bid) {
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
		rStr = UrlUtil.getStringByGetNotEncod(solr_url + PRODUCT_URL + SEARCH_URL, param);

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
		param.put("sort", "sf+desc");// 按照分数来推荐
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
		String ssss = str.substring(index + 7, str.length() - 2).replace("\"[", "[")
							.replace("]\"", "]").replace("\\", "");
		return ssss;
	}

	@SuppressWarnings("deprecation")
	public List<Integer> getOrderIdsFromSolr(Integer userId, String status) {
		List<Integer> list = new ArrayList<Integer>();
		HttpSolrServer solrServer = getServer(ORDER_URL);
		SolrQuery sQuery = new SolrQuery();
		String para = "";
		if (userId != null) {
			para = para + "user_id:" + userId;
		}
		// 10待支付；11支付中；12已支付；13缺少身份证；14已有身份证；20已发货；21国外清关；30国内清关；40国内物流；50已关闭；60已完成；70已删除；
		if (StringUtils.isNotEmpty(status)) {
			if (status.equals("finsh")) {
				para = para + " AND status:60";
			} else {
				para = para + " AND -status:60";
			}
		}

		// 查询关键词，*:*代表所有属性、所有值，即所有index
		if (!StringUtils.isNotEmpty(para)) {
			para = "*:*";
		}
		sQuery.setQuery(para);
		sQuery.setStart(0);
		sQuery.setRows(10);
		// 排序 如果按照blogId 排序，，那么将blogId desc(or asc) 改成 id desc(or asc)
		sQuery.addSortField("gmt_modify", ORDER.desc);

		try {
			QueryResponse response = solrServer.query(sQuery);
			SolrDocumentList doclist = response.getResults();
			Integer orderId = null;
			for (SolrDocument solrDocument : doclist) {
				orderId = solrDocument.getFieldValue("id") != null ? Integer.valueOf(solrDocument.getFieldValue(
						"id").toString())
						: null;
				if (orderId != null) {
					list.add(orderId);
				}
			}
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		return list;
	}
}
