
package com.flyhz.avengers.framework.common.entity;

import java.io.Serializable;

public class ProductEntity implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -709467989643153816L;

	private String				id;

	private String				name;

	private String				description;

	private String				brand;

	private String				category;

	private String				style;

	private String				orignialPrice;

	private String				presentPrice;

	// n(name) d(description) b(brand) c(category) s(style) op(original_price)
	// pp(present_price) co(color) coi(color_img) pi(product_img) u(url)
	// bid(batchId)

}
