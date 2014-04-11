
package com.holding.smile.protocol;

import java.util.List;

import com.holding.smile.entity.Category;

public class PCategory extends PBase {
	private List<Category>	data;

	public List<Category> getData() {
		return data;
	}

	public void setData(List<Category> data) {
		this.data = data;
	}

}
