
package com.flyhz.shop.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.flyhz.framework.lang.CacheRepository;
import com.flyhz.framework.util.Constants;
import com.flyhz.framework.util.UrlUtil;
import com.flyhz.shop.build.solr.Fraction;
import com.flyhz.shop.build.solr.PageModel;
import com.flyhz.shop.build.solr.ProductFraction;
import com.flyhz.shop.build.solr.SolrServer;
import com.flyhz.shop.dto.ProductBuildDto;
import com.flyhz.shop.persistence.dao.ProductDao;
import com.flyhz.shop.service.BuildService;

@Service
public class BuildServiceImpl implements BuildService {

	@Resource
	private ProductDao		productDao;
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

			int maxIdCount = getCountOfAll();
			// 500条数据查询一次并插入数据库
			int resultSize = 500;
			int thisNum = 0;
			List<ProductBuildDto> productList = null;

			System.out.println("solr start...");
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

					fraction.setLastUpadteTime(product.getT());
					doc.addField("sf", productFraction.getProductFraction(fraction).intValue());// 分数排序
					doc.addField("st", i);// 时间排序

					docs.add(doc);

					System.out.println(cacheRepository.getObject(String.valueOf(product.getId()),
							product.getClass()));
					// cacheRepository.set(String.valueOf(product.getId()),
					// product);
				}

				// 设置新的分页查询参数
				thisNum += resultSize;

				// 提交到solr
				solrServer = SolrServer.getServer();
				solrServer.add(docs);
				solrServer.commit();
			}
			System.out.println("solr end");

			System.out.println("redis start...");
			// do some things
			System.out.println("redis end");

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("buildData结束");
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

	private List<ProductBuildDto> getProductsByCidBid(Integer cid, Integer bid) {
		List<ProductBuildDto> list = new ArrayList<ProductBuildDto>();

		String rStr = "";
		HashMap<String, String> param = new HashMap<String, String>();

		param.put("q", "*%3A*");
		// param.put("fq", "seqorder_time%3A%5B" + (seqorder + 1) + "+TO+*%5D");

		param.put("sort", "seqorder_time+desc");
		param.put("wt", "json");
		param.put("fl", Constants.PARAM_STRING);
		param.put("rows", "10");
		rStr = UrlUtil.getStringByGet(server_url + Constants.SEARCH_URL, param);

		// if (rStr != null && !"".equals(rStr)) {
		// JSONObject datas;
		// try {
		// datas = new JSONObject(rStr);
		// // .replaceAll("\"\\\\[", "[").replaceAll("\\\\]\"", "]")
		// String docs = datas.getJSONObject("response").getString("docs");
		// int numFound = datas.getJSONObject("response").getInt("numFound");
		// SolrGood[] solr = JSONUtil.getJson2Entity(docs, SolrGood[].class);
		// if (solr != null) {
		// for (int i = 0; i < solr.length; i++) {
		// results.add(new JGoods(solr[i]));
		// }
		// }
		//
		// ValidateDto vd = new ValidateDto();
		// if (results.size() == 0) {
		// if (refresh) {
		// vd.setMessage(Constants.MESSAGE_NODATA);
		// } else {
		// vd.setMessage(Constants.MESSAGE_LAST);
		// }
		// }
		// if (refresh && numFound > Constants.INIT_NUM) {
		// vd.setOption("3");
		// }
		// rvd.setValidate(vd);
		// } catch (JSONException e) {
		// e.printStackTrace();
		// ValidateDto vd = new ValidateDto();
		// vd.setMessage(Constants.MESSAGE_EXCEPTION);
		// rvd.setValidate(vd);
		// }
		// } else {
		// ValidateDto vd = new ValidateDto();
		// vd.setMessage(Constants.MESSAGE_NET);
		// rvd.setValidate(vd);
		// }

		return list;
	}
}
