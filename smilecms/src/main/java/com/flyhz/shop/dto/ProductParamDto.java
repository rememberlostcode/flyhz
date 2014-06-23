
package com.flyhz.shop.dto;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

/**
 * 产品参数DTO
 * 
 * @author Administrator
 */
public class ProductParamDto {
	private Integer				id;
	private Integer				brandId;
	private String				brandstyle;
	private Integer				categoryId;
	private String				description;
	private String				creator;
	private String				dataSrc;
	private String				offShelf;
	private List<MultipartFile>	imgs;
	private BigDecimal			localprice;		// 国内价格
	private String				name;
	private BigDecimal			purchasingprice;	// 现在价格
	private BigDecimal			foreighprice;		// 国外价格
	private BigDecimal			recommendprice;	// 推荐价格
	private String				sizedesc;			// 产品尺码描述
	private String				color;
	private MultipartFile		colorimg;
	private String				oldColorimg;		// 旧颜色图片地址
	private List<String>		productImgs;		// 产品旧cover图片
	private List<String>		productSrcImgs;	// 产品旧imgs

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

	public List<MultipartFile> getImgs() {
		return imgs;
	}

	public void setImgs(List<MultipartFile> imgs) {
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

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public MultipartFile getColorimg() {
		return colorimg;
	}

	public void setColorimg(MultipartFile colorimg) {
		this.colorimg = colorimg;
	}

	public String getOldColorimg() {
		return oldColorimg;
	}

	public void setOldColorimg(String oldColorimg) {
		this.oldColorimg = oldColorimg;
	}

	public List<String> getProductImgs() {
		return productImgs;
	}

	public void setProductImgs(List<String> productImgs) {
		this.productImgs = productImgs;
	}

	public List<String> getProductSrcImgs() {
		return productSrcImgs;
	}

	public void setProductSrcImgs(List<String> productSrcImgs) {
		this.productSrcImgs = productSrcImgs;
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
}
