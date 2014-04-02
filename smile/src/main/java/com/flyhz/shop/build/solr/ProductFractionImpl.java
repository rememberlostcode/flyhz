
package com.flyhz.shop.build.solr;

public class ProductFractionImpl implements ProductFraction {

	@Override
	public int getProductFraction(Fraction fraction) {
		return (int) (fraction.getLastUpadteTime().getTime() / 1000000000 + fraction.getProductId());
	}
}
