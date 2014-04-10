
package com.flyhz.shop.build.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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

		for (int i = 0; i < productList.size(); i++) {
			product = productList.get(i);
			doc = new SolrInputDocument();
			doc.addField("id", product.getId());// ID
			doc.addField("n", product.getN());// 名称
			doc.addField("d", product.getD());// 说明
			doc.addField("lp", product.getLp());// 本地价格
			doc.addField("pp", product.getPp());// 代购价格

			// 计算差价
			if (product.getLp() != null && product.getPp() != null) {
				doc.addField("sp", product.getLp().subtract(product.getPp()));
			} else {
				doc.addField("sp", 0);
			}
			doc.addField("p", product.getP());// 封面
			doc.addField("t", DateUtil.strToDateLong(product.getT()));// 时间
			doc.addField("bid", product.getBid());// 品牌ID
			doc.addField("be", product.getBe());// 品牌名称
			doc.addField("cid", product.getCid());// 分类ID

			doc.addField("sf", productFraction.getProductFraction(product));// 分数
			doc.addField("st", product.getSt());// 时间排序值
			doc.addField("sd", product.getSd());// 折扣排序值
			doc.addField("ss", product.getSs());// 销售量排序值
			doc.addField("sn", product.getSn());// 销售量
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
		// RProductDto[] sz = JSONUtil.getJson2Entity(ssss,
		// RProductDto[].class);
		// System.out.println(sz);
		return ssss;
	}
}
