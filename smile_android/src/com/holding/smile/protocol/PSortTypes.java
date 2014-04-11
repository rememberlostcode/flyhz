
package com.holding.smile.protocol;

import java.util.List;

import com.holding.smile.entity.SortType;

public class PSortTypes extends PBase {
	private List<SortType>	data;

	public List<SortType> getData() {
		return data;
	}

	public void setData(List<SortType> data) {
		this.data = data;
	}
}
