
package com.flyhz.shop.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.flyhz.shop.persistence.dao.CurrencyDao;
import com.flyhz.shop.service.CurrencyService;

@Service
public class CurrencyServiceImpl implements CurrencyService {
	@Resource
	private CurrencyDao	currencyDao;

	@Override
	public List<String> getCurrencyShorts() {
		return currencyDao.getCurrencyShorts();
	}
}
