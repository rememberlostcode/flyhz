
package com.holding.smile.entity;

import java.io.Serializable;
import java.util.List;

public class IndexBrands implements Serializable {

	private static final long	serialVersionUID	= 1L;
	private List<JActivity>		activitys;
	private List<Brand>			brands;

	public List<JActivity> getActivitys() {
		return activitys;
	}

	public void setActivitys(List<JActivity> activitys) {
		this.activitys = activitys;
	}

	public List<Brand> getBrands() {
		return brands;
	}

	public void setBrands(List<Brand> brands) {
		this.brands = brands;
	}


}
