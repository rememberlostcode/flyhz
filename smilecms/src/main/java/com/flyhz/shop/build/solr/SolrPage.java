
package com.flyhz.shop.build.solr;

import java.util.Date;

/**
 * 分页对象，用于solr建立索引
 * @author zfsoft_zb
 *
 */
public class SolrPage {

	private Integer	start;
	private Integer	num;
	private Date	startDate;
	private Date	endDate;

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
