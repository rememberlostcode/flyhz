
package com.flyhz.shop.build.solr;

import java.util.Date;

/**
 * 分数，solr计算商品的分数用于排序
 * 
 * @author zhangb 2014年4月1日 下午1:15:41
 * 
 */
public class Fraction {

	/**
	 * 已购买数
	 */
	private int		purchasedNum;
	/**
	 * 总点击数
	 */
	private int		clickNum;
	/**
	 * 最后更新时间
	 */
	private Date	lastUpadteTime;

	public int getPurchasedNum() {
		return purchasedNum;
	}

	public void setPurchasedNum(int purchasedNum) {
		this.purchasedNum = purchasedNum;
	}

	public int getClickNum() {
		return clickNum;
	}

	public void setClickNum(int clickNum) {
		this.clickNum = clickNum;
	}

	public Date getLastUpadteTime() {
		return lastUpadteTime;
	}

	public void setLastUpadteTime(Date lastUpadteTime) {
		this.lastUpadteTime = lastUpadteTime;
	}
}
