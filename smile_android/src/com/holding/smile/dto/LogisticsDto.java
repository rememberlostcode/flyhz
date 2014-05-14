
package com.holding.smile.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LogisticsDto implements Serializable {

	private static final long	serialVersionUID	= 5774993847067316875L;

	List<String>				list;

	public LogisticsDto() {
		list = new ArrayList<String>();
		list.add("2014-02-28 13:04:50卖家已发货");
		list.add("2014-02-28 18:20:41快件离开萧山已发往杭州");
		list.add("2014-02-28 18:20:43快件到达萧山上一站是萧山追爱");
		list.add("2014-02-28 18:20:52快件离开萧山已发往杭州");
		list.add("2014-02-28 18:35:07快件离开萧山已发往杭州中转部");
		list.add("2014-02-28 21:10:50快件到达杭州中转部上一站是未通达");
		list.add("2014-02-28 21:11:21快件离开杭州中转部已发往杭州操作部");
		list.add("2014-02-28 21:29:26快件到达杭州操作部上一站是杭州中转部");
		list.add("2014-02-28 21:38:49快件离开杭州操作部已发往杭州拱震桥");
		list.add("2014-03-01 19:01:29快件已签收签收人拍照签收");
		list.add("2014-03-02 09:23:21快件到达杭州拱震桥上一站是杭州");
		list.add("2014-03-02 09:23:32杭州拱震桥的徐彦斌正在派件");
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}
}
