
package com.flyhz.shop.dto;

import java.io.Serializable;
import java.util.List;

import com.flyhz.shop.persistence.entity.LogisticsModel;

/**
 * 物流信息
 * 
 * @author zhangb
 * 
 */
public class LogisticsDto extends LogisticsModel implements Serializable {

	private static final long	serialVersionUID	= 5774993847067316875L;

	private List<String>		transitStepInfoList;

	public List<String> getTransitStepInfoList() {
		return transitStepInfoList;
	}

	public void setTransitStepInfoList(List<String> transitStepInfoList) {
		this.transitStepInfoList = transitStepInfoList;
	}

}
