
package com.flyhz.shop.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 管理员后台产品DTO
 * 
 * @author Administrator
 */
public class ProductCmsDto {
	private Integer			id;				// 主键ID
	private String			name;				// 产品名称
	private String			color;				// 颜色
	private String			brandstyle;		// 款号
	private String			cateName;			// 分类名称
	private String			brandName;			// 品牌名称
	private BigDecimal		purchasingprice;	// 代购价格
	private String			description;		// 描述
	private String			cover;				// 产品详情图
	private String			coverSmall;		// APP列表图
	private String			recommendprice;	// 推荐价格
	private String			offShelf;			// 是否下架
	private List<String>	appImages;			// APP列表图
	private List<String>	productImgs;		// 产品详情图

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getBrandstyle() {
		return brandstyle;
	}

	public void setBrandstyle(String brandstyle) {
		this.brandstyle = brandstyle;
	}

	public String getCateName() {
		return cateName;
	}

	public void setCateName(String cateName) {
		this.cateName = cateName;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public BigDecimal getPurchasingprice() {
		return purchasingprice;
	}

	public void setPurchasingprice(BigDecimal purchasingprice) {
		this.purchasingprice = purchasingprice;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public String getCoverSmall() {
		return coverSmall;
	}

	public void setCoverSmall(String coverSmall) {
		this.coverSmall = coverSmall;
	}

	public String getRecommendprice() {
		return recommendprice;
	}

	public void setRecommendprice(String recommendprice) {
		this.recommendprice = recommendprice;
	}

	public List<String> getAppImages() {
		return appImages;
	}

	public void setAppImages(List<String> appImages) {
		this.appImages = appImages;
	}

	public List<String> getProductImgs() {
		return productImgs;
	}

	public void setProductImgs(List<String> productImgs) {
		this.productImgs = productImgs;
	}

	public String getOffShelf() {
		return offShelf;
	}

	public void setOffShelf(String offShelf) {
		this.offShelf = offShelf;
	}
}
