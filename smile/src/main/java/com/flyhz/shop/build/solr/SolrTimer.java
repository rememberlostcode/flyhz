
package com.flyhz.shop.build.solr;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.flyhz.framework.util.DateUtil;
import com.flyhz.framework.util.JSONUtil;
import com.flyhz.shop.dto.OrderDetailDto;
import com.flyhz.shop.dto.OrderDto;
import com.flyhz.shop.persistence.dao.OrderDao;
import com.flyhz.shop.persistence.dao.SalesvolumeDao;
import com.flyhz.shop.persistence.entity.SalesvolumeModel;

public class SolrTimer extends QuartzJobBean {
	private OrderDao		orderDao;
	private SalesvolumeDao	salesvolumeDao;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		int mysqlSize = 500;// 每次取500条
		orderDao = (OrderDao) context.getMergedJobDataMap().get("orderDao");
		salesvolumeDao = (SalesvolumeDao) context.getMergedJobDataMap().get("salesvolumeDao");

		SolrPage solrPage = new SolrPage();
		Date date = new Date();

		Date lastEndDate = salesvolumeDao.getLastEndDate();
		if (lastEndDate == null) {
			lastEndDate = DateUtil.strToDateLong("1900-01-01 00:00:00");
		}

		Map<Integer, Short> prdouctSaleNumbers = new HashMap<Integer, Short>();
		// 500条数据查询一次并插入数据库
		int orderStart = 0;
		solrPage.setStartDate(lastEndDate);
		solrPage.setEndDate(date);

		int maxOrderCount = orderDao.getFinshedOrdersCount(solrPage);
		List<String> detailList = null;
		OrderDto orderDto = null;
		List<OrderDetailDto> detailDtoList = null;
		OrderDetailDto detailDto = null;
		Integer productId = null;
		solrPage.setNum(mysqlSize);

		while (orderStart < maxOrderCount) {
			solrPage.setStart(orderStart);
			detailList = orderDao.findFinshedOrders(solrPage);
			for (int i = 0; i < detailList.size(); i++) {
				if (detailList.get(i) != null) {
					// 转换后获取商品购买数量
					orderDto = JSONUtil.getJson2Entity(detailList.get(i), OrderDto.class);
					detailDtoList = orderDto.getDetails();
					for (int j = 0; j < detailDtoList.size(); j++) {
						detailDto = detailDtoList.get(j);
						if (detailDto != null && detailDto.getProduct() != null) {
							productId = detailDto.getProduct().getId();
							if (prdouctSaleNumbers.get(productId) == null) {
								prdouctSaleNumbers.put(productId, detailDto.getQty());
							} else {
								prdouctSaleNumbers.put(
										productId,
										(short) (prdouctSaleNumbers.get(productId) + detailDto.getQty()));
							}
						}

					}
				}
			}

			// 设置新的分页查询参数
			orderStart += mysqlSize;
		}

		List<SalesvolumeModel> salesList = new ArrayList<SalesvolumeModel>();
		Set<Integer> keySet = prdouctSaleNumbers.keySet();
		Iterator<Integer> iter = keySet.iterator();
		while (iter.hasNext()) {
			Integer key = iter.next();
			SalesvolumeModel salesvolumeModel = new SalesvolumeModel();
			salesvolumeModel.setProductId(key);
			salesvolumeModel.setTotalnumber(prdouctSaleNumbers.get(key).intValue());
			salesvolumeModel.setGmtCreate(date);
			salesvolumeModel.setGmtModify(date);
			salesvolumeModel.setTimeStart(lastEndDate);
			salesvolumeModel.setTimeEnd(date);
			salesList.add(salesvolumeModel);
		}
		if (salesList.size() > 0) {
			salesvolumeDao.batchInsert(salesList);
		}
	}
}
