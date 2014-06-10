
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
	private List<String>		productImgs;

	public List<String> getProductImgs() {
		return productImgs;
	}

	public void setProductImgs(List<String> productImgs) {
		this.productImgs = productImgs;
	}
}
