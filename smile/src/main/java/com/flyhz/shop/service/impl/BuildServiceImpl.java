
package com.flyhz.shop.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.flyhz.framework.redis.CacheRepository;
import com.flyhz.framework.solr.PageModel;
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

	@Override
	public void buildData() {

		System.out.println("buildData开始...");

		try {
			System.out.println("redis start...");
			int maxIdCount = getCountOfAll();
			// 500条数据查询一次并插入数据库
			int resultSize = 500;
			int thisNum = 0;
			List<ProductBuildDto> newsList = null;
			while (thisNum < maxIdCount) {
				newsList = findAll(thisNum, resultSize);
				ProductBuildDto newsModel = null;
				for (int i = 0; i < newsList.size(); i++) {
					newsModel = newsList.get(i);
					// cacheRepository.set(String.valueOf(newsModel.getId()),
					// newsModel);
				}
				// 设置新的分页查询参数
				thisNum += resultSize;
			}
			System.out.println("redis end");

			System.out.println("solr start...");
			// UrlUtil.sendGet(server_url
			// + "/solr/collection1/dataimport?full-import&commit=y&clean=y");
			System.out.println("solr end");

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
