
package com.flyhz.shop.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.flyhz.framework.lang.CacheRepository;
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
					doc.addField("i", product.getId());
					doc.addField("n", product.getName());
					doc.addField("lp", product.getLocalprice());
					doc.addField("pp", product.getPurchasingprice());
					doc.addField("p", product.getImgs());

					fraction.setLastUpadteTime(product.getGmtModify());
					doc.addField("fraction", productFraction.getProductFraction(fraction));

					docs.add(doc);
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

}
