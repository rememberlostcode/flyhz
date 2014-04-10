
package com.flyhz.shop.build.solr;

import com.flyhz.shop.dto.ProductBuildDto;

public class ProductFractionImpl implements ProductFraction {

	@Override
	public double getProductFraction(ProductBuildDto product) {
		int sn = product.getSn() == null ? 0 : product.getSn();
		int zsn = product.getSn() == null ? 0 : product.getSn();
		double id = product.getId() == null ? 0.0 : (double) product.getId();
		return (double) (Math.round((id / 10000 + sn * 5 + zsn * 10) * 10000) / 10000.0);
	}
}
