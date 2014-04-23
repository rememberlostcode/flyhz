
package com.holding.smile.protocol;

import com.holding.smile.dto.ProductDto;

/**
 * 类说明：更改商品信息
 * 
 * @author robin 2014-4-22下午3:26:09
 * 
 */
public class PProduct extends PBase {

	private ProductDto	data;

	public ProductDto getData() {
		return data;
	}

	public void setData(ProductDto data) {
		this.data = data;
	}

}
