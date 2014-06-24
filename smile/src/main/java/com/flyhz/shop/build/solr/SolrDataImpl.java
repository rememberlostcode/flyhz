
package com.flyhz.shop.build.solr;

import java.io.IOException;
import java.math.BigDecimal;
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
import com.flyhz.framework.util.Constants;
import com.flyhz.framework.util.DateUtil;
import com.flyhz.framework.util.StringUtil;
import com.flyhz.framework.util.UrlUtil;
import com.flyhz.shop.dto.LogisticsDto;
import com.flyhz.shop.dto.OrderSimpleDto;
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

	/**
	 * 通过商品对象获得SolrInputDocument
	 * 
	 * @param productBuildDto
	 * @return
	 */
	private SolrInputDocument getDocumentByProduct(ProductBuildDto productBuildDto) {
		SolrInputDocument doc = new SolrInputDocument();
		String[] pictures = null;
		doc.addField("id", productBuildDto.getId());// ID
		doc.addField("n", productBuildDto.getN());// 名称
		doc.addField("d", productBuildDto.getD());// 说明
		doc.addField("bs", productBuildDto.getBs());// 款号

		// 调整本地价格及计算差价（即折扣）
		if (productBuildDto.getLp() != null && productBuildDto.getPp() != null) {
			doc.addField("sp", productBuildDto.getLp().subtract(productBuildDto.getPp()));
		} else {
			if (productBuildDto.getPp() == null) {
				log.error("商品ID为" + productBuildDto.getId() + "的商品的代购价为空，不可build到solr");
				return null;
			} else {
				if(productBuildDto.getForeighprice()==null){
					productBuildDto.setLp(BigDecimal.valueOf(productBuildDto.getPp().doubleValue() * 2 + 1000));
				} else {
					if(Constants.dollarExchangeRate == null){
						log.info("获取美元汇率...");
						Constants.dollarExchangeRate = getDollarExchangeRate();
						log.info("获取美元汇率完成，汇率为"+Constants.dollarExchangeRate);
					}
					productBuildDto.setLp(BigDecimal.valueOf(productBuildDto.getForeighprice().doubleValue() * Constants.dollarExchangeRate * 2 + 1000));
					
				}
				doc.addField("sp", productBuildDto.getLp().subtract(productBuildDto.getPp()));
			}
		}
		doc.addField("lp", productBuildDto.getLp().intValue());// 本地价格
		doc.addField("pp", productBuildDto.getPp().intValue());// 代购价格

		// 原图封面
		if (productBuildDto.getImgs() != null) {
			pictures = productBuildDto.getImgs().replace("[", "").replace("]", "")
										.replace("\"", "").split(",");
			for (int k = 0; k < pictures.length; k++) {
				doc.addField("imgs", pictures[k]);
			}
		}
		// 大图封面
		if (productBuildDto.getBp() != null) {
			pictures = productBuildDto.getBp().replace("[", "").replace("]", "").replace("\"", "")
										.split(",");
			for (int k = 0; k < pictures.length; k++) {
				doc.addField("bp", pictures[k]);
			}
		}
		// 小图封面
		if (productBuildDto.getP() != null) {
			pictures = productBuildDto.getP().replace("[", "").replace("]", "").replace("\"", "")
										.split(",");
			for (int k = 0; k < pictures.length; k++) {
				doc.addField("p", pictures[k]);
			}
		}

		doc.addField("t", DateUtil.strToDateLong(productBuildDto.getT()));// 时间
		doc.addField("bid", productBuildDto.getBid());// 品牌ID
		doc.addField("be", productBuildDto.getBe());// 品牌名称
		doc.addField("cid", productBuildDto.getCid());// 分类ID
		doc.addField("ce", productBuildDto.getCe());// 分类名称

		doc.addField("c", productBuildDto.getC());// 颜色名称
		doc.addField("ci", productBuildDto.getCi());// 颜色图片

		doc.addField("sf", productFraction.getProductFraction(productBuildDto));// 分数
		doc.addField("st", productBuildDto.getSt());// 时间排序值
		doc.addField("sd", productBuildDto.getSd());// 折扣排序值
		doc.addField("ss", productBuildDto.getSs());// 总销售量排序值
		doc.addField("sy", productBuildDto.getSy());// 月销售量排序值
		doc.addField("sj", productBuildDto.getSj());// 价格排序值

		doc.addField("sn", productBuildDto.getSn() != null ? productBuildDto.getSn() : 0);// 销售量
		doc.addField("zsn", productBuildDto.getZsn() != null ? productBuildDto.getZsn() : 0);// 当月销售量

		return doc;
	}

	public void submitProduct(ProductBuildDto productBuildDto) {
		SolrInputDocument doc = getDocumentByProduct(productBuildDto);

		// 提交到solr
		HttpSolrServer solrServer = getServer(PRODUCT_URL);
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

	public void submitProductList(List<ProductBuildDto> productList) {
		Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();

		SolrInputDocument doc = null;
		for (int i = 0; i < productList.size(); i++) {
			doc = getDocumentByProduct(productList.get(i));
			if (doc != null) {
				docs.add(doc);
			}
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

	public void removeProduct(String productId) {
		// 提交到solr
		HttpSolrServer solrServer = getServer(PRODUCT_URL);
		try {
			solrServer.deleteById(productId);
			solrServer.commit();
		} catch (SolrServerException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		} finally {
			// solrServer.shutdown();
		}
	}

	public void submitOrder(Integer userId, Integer orderId, String status, Date gmtModify,
			LogisticsDto logisticsDto) {
		if (StringUtil.isBlank(status)) {
			status = Constants.OrderStateCode.FOR_PAYMENT.code;
		}
		if (gmtModify == null) {
			gmtModify = new Date();
		}
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", orderId);
		doc.addField("user_id", userId);
		doc.addField("status", status);
		doc.addField("gmt_modify", gmtModify);

		if (logisticsDto != null) {
			doc.addField("address", logisticsDto.getAddress());
			doc.addField("logisticsStatus", logisticsDto.getLogisticsStatus());
			doc.addField("companyName", logisticsDto.getCompanyName());
			doc.addField("tid", logisticsDto.getTid());
			if (logisticsDto.getTransitStepInfoList() != null
					&& logisticsDto.getTransitStepInfoList().size() > 0) {
				for (int i = 0; i < logisticsDto.getTransitStepInfoList().size(); i++) {
					doc.addField("transitStepInfoList", logisticsDto.getTransitStepInfoList()
																	.get(i));
				}
			}
		}

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
	public List<OrderSimpleDto> getOrderIdsFromSolr(Integer userId, String status) {
		List<OrderSimpleDto> list = new ArrayList<OrderSimpleDto>();
		HttpSolrServer solrServer = getServer(ORDER_URL);
		SolrQuery sQuery = new SolrQuery();
		String para = "";
		if (userId != null) {
			para = para + "user_id:" + userId;
		}
		if (StringUtils.isNotEmpty(status)) {
			if (status.equals("finsh")) {
				para = para + " AND status:" + Constants.OrderStateCode.HAS_BEEN_COMPLETED.code;// 等于已完成的
			} else {
				para = para + " AND -status:" + Constants.OrderStateCode.HAS_BEEN_COMPLETED.code;// 不等于已完成的
			}
		}

		// 查询关键词，*:*代表所有属性、所有值，即所有index
		if (!StringUtils.isNotEmpty(para)) {
			para = "*:*";
		}
		sQuery.setQuery(para);
		sQuery.setStart(0);
		sQuery.setRows(10);
		sQuery.addSortField("gmt_modify", ORDER.desc);

		try {
			QueryResponse response = solrServer.query(sQuery);
			SolrDocumentList doclist = response.getResults();

			Integer orderId = null;
			String logisticsStatus = null;
			Long tid = null;
			String companyName = null;
			String address = null;
			for (SolrDocument solrDocument : doclist) {
				orderId = solrDocument.getFieldValue("id") != null ? Integer.valueOf(solrDocument.getFieldValue(
						"id").toString())
						: null;
				address = solrDocument.getFieldValue("address") != null ? solrDocument.getFieldValue(
						"address").toString()
						: null;
				status = solrDocument.getFieldValue("status") != null ? solrDocument.getFieldValue(
						"status").toString() : null;

				logisticsStatus = solrDocument.getFieldValue("logisticsStatus") != null ? solrDocument.getFieldValue(
						"logisticsStatus").toString()
						: null;
				tid = solrDocument.getFieldValue("tid") != null ? Long.valueOf(solrDocument.getFieldValue(
						"tid").toString())
						: null;
				companyName = solrDocument.getFieldValue("companyName") != null ? solrDocument.getFieldValue(
						"companyName").toString()
						: null;
				if (orderId != null) {
					OrderSimpleDto or = new OrderSimpleDto();
					LogisticsDto logisticsDto = new LogisticsDto();
					or.setId(orderId);
					or.setStatus(status);
					logisticsDto.setLogisticsStatus(logisticsStatus);
					logisticsDto.setTid(tid);
					logisticsDto.setCompanyName(companyName);
					logisticsDto.setAddress(address);

					if (solrDocument.getFieldValues("transitStepInfoList") != null) {
						List<String> newlist = new ArrayList<String>();
						Object obj[] = solrDocument.getFieldValues("transitStepInfoList").toArray(); // 将所有的内容变为对象数组
						for (int x = 0; x < obj.length; x++) {
							newlist.add(String.valueOf(obj[x]));
						}
						logisticsDto.setTransitStepInfoList(newlist);
					}
					or.setLogisticsDto(logisticsDto);
					list.add(or);
				}
			}
		} catch (SolrServerException e) {
			log.error(e.getMessage());
		}
		return list;
	}

	public void cleanProduct() {
		// 提交到solr
		HttpSolrServer solrServer = getServer(PRODUCT_URL);
		try {
			solrServer.deleteByQuery("*:*");
			solrServer.commit();
		} catch (SolrServerException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		} finally {
			// solrServer.shutdown();
		}
	}
	
	public double getDollarExchangeRate() {
		log.info("开始获取美元汇率...");
		double dollarExchangeRate = 6.50;
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
