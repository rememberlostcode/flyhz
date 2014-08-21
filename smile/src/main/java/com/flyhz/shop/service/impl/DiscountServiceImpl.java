
package com.flyhz.shop.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.flyhz.shop.persistence.dao.DiscountDao;
import com.flyhz.shop.persistence.entity.DiscountModel;
import com.flyhz.shop.service.DiscountService;

/**
 * 
 * 类说明：折扣
 * 
 * @author robin 2014-8-21下午2:48:00
 * 
 */
@Service
public class DiscountServiceImpl implements DiscountService {

	@Resource
	private DiscountDao	discountDao;

	@Override
	public DiscountModel getDiscountModelById(Integer did) {
		if (did == null)
			return null;
		return discountDao.getModelById(did);
	}

}
