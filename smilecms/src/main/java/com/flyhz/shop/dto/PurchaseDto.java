
package com.flyhz.shop.dto;

import java.util.List;

import com.flyhz.shop.persistence.entity.PurchaseModel;

public class PurchaseDto extends PurchaseModel {
	private static final long	serialVersionUID	= 1L;

	private String				currency;					// 币种
	private String				productName;				// 产品名称
	private String				brandName;					// 产品品牌
	private String				brandstyle;				// 产品款号
	private String				coverSmall;				// APP列表图
	private List<String>		appImages;					// 产品小图

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getBrandstyle() {
		return brandstyle;
	}

	public void setBrandstyle(String brandstyle) {
		this.brandstyle = brandstyle;
	}

	public String getCoverSmall() {
		return coverSmall;
	}

	public void setCoverSmall(String coverSmall) {
		this.coverSmall = coverSmall;
	}

	public List<String> getAppImages() {
		return appImages;
	}

	public void setAppImages(List<String> appImages) {
		this.appImages = appImages;
	}
}
