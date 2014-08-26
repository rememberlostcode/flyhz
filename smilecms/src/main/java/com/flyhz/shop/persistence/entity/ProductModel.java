
package com.flyhz.shop.persistence.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * The persistent class for the product database table.
 * 
 */

public class ProductModel implements Serializable {
	private static final long	serialVersionUID	= 1L;
	private Integer				id;
	private Integer				brandId;
	private String				brandstyle;
	private Integer				categoryId;
	private String				description;
	private Date				gmtCreate;
	private Date				gmtModify;
	private String				imgs;
	private BigDecimal			localprice;				// 国内价格
	private String				name;
	private BigDecimal			purchasingprice;			// 现在价格
	private BigDecimal			foreighprice;				// 国外价格
	private BigDecimal			recommendprice;			// 推荐价格
	private String				style;
	private String				color;
	private String				colorimg;
	private String				cover;
	private String				coverSmall;
	private String				creator;
	private String				dataSrc;					// 数据来源：manual--手动；spider--爬虫
	private String				offShelf;					// 是否下架：y--是；n--否
	private String				sizedesc;					// 产品尺码描述
	private String				currency;					// 币种
	private BigDecimal			originprice;				// 产品原始价格
	private BigDecimal			discountprice;				// 产品折扣价格

	public ProductModel() {

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getBrandId() {
		return brandId;
	}

	public void setBrandId(Integer brandId) {
		this.brandId = brandId;
	}

	public String getBrandstyle() {
		return brandstyle;
	}

	public void setBrandstyle(String brandstyle) {
		this.brandstyle = brandstyle;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModify() {
		return gmtModify;
	}

	public void setGmtModify(Date gmtModify) {
		this.gmtModify = gmtModify;
	}

	public String getImgs() {
		return imgs;
	}

	public void setImgs(String imgs) {
		this.imgs = imgs;
	}

	public BigDecimal getLocalprice() {
		return localprice;
	}

	public void setLocalprice(BigDecimal localprice) {
		this.localprice = localprice;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPurchasingprice() {
		return purchasingprice;
	}

	public void setPurchasingprice(BigDecimal purchasingprice) {
		this.purchasingprice = purchasingprice;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getColorimg() {
		return colorimg;
	}

	public void setColorimg(String colorimg) {
		this.colorimg = colorimg;
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

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getDataSrc() {
		return dataSrc;
	}

	public void setDataSrc(String dataSrc) {
		this.dataSrc = dataSrc;
	}

	public String getOffShelf() {
		return offShelf;
	}

	public void setOffShelf(String offShelf) {
		this.offShelf = offShelf;
	}

	public BigDecimal getForeighprice() {
		return foreighprice;
	}

	public void setForeighprice(BigDecimal foreighprice) {
		this.foreighprice = foreighprice;
	}

	public BigDecimal getRecommendprice() {
		return recommendprice;
	}

	public void setRecommendprice(BigDecimal recommendprice) {
		this.recommendprice = recommendprice;
	}

	public String getSizedesc() {
		return sizedesc;
	}

	public void setSizedesc(String sizedesc) {
		this.sizedesc = sizedesc;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getOriginprice() {
		return originprice;
	}

	public void setOriginprice(BigDecimal originprice) {
		this.originprice = originprice;
	}

	public BigDecimal getDiscountprice() {
		return discountprice;
	}

	public void setDiscountprice(BigDecimal discountprice) {
		this.discountprice = discountprice;
	}
}