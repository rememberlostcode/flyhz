
package com.flyhz.shop.build.solr;

public class ProductFractionImpl implements ProductFraction {

	@Override
	public Double getProductFraction(Fraction fraction) {
		return (double) fraction.getLastUpadteTime().getTime();
	}
}
