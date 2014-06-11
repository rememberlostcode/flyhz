
package com.flyhz.shop.dto;

import java.util.List;

import com.flyhz.shop.persistence.entity.ProductModel;

/**
 * 产品展示DTO
 * 
 * @author fuwb
 */
public class ProductShowDto extends ProductModel {
	private static final long	serialVersionUID	= 1L;
	private Integer				productImgsCount;
	private List<String>		productImgs;
	private List<String>		productSrcImgs;

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

	public Integer getProductImgsCount() {
		return productImgsCount;
	}

	public void setProductImgsCount(Integer productImgsCount) {
		this.productImgsCount = productImgsCount;
	}
}
