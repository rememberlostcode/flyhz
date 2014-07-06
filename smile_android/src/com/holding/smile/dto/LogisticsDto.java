
package com.holding.smile.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 物流信息
 * 
 * @author zhangb
 * 
 */
public class LogisticsDto implements Serializable {

	private static final long	serialVersionUID	= 5774993847067316875L;
	/**
	 * 详细地址
	 */
	protected String			address;
	/**
	 * 快递公司
	 */
	private String				companyName;
	/**
	 * 物流状态描述
	 */
	private String				logisticsStatus;
	/**
	 * 运单号
	 */
	private Long				tid;
	private List<String>		transitStepInfoList;

//	public LogisticsDto() {
//		transitStepInfoList = new ArrayList<String>();
//		transitStepInfoList.add("2014-02-28 13:04:50卖家已发货");
//		transitStepInfoList.add("2014-02-28 18:20:41快件离开萧山已发往杭州");
//		transitStepInfoList.add("2014-02-28 18:20:43快件到达萧山上一站是萧山追爱");
//		transitStepInfoList.add("2014-02-28 18:20:52快件离开萧山已发往杭州");
//		transitStepInfoList.add("2014-02-28 18:35:07快件离开萧山已发往杭州中转部");
//		transitStepInfoList.add("2014-02-28 21:10:50快件到达杭州中转部上一站是未通达");
//		transitStepInfoList.add("2014-02-28 21:11:21快件离开杭州中转部已发往杭州操作部");
//		transitStepInfoList.add("2014-02-28 21:29:26快件到达杭州操作部上一站是杭州中转部");
//		transitStepInfoList.add("2014-02-28 21:38:49快件离开杭州操作部已发往杭州拱震桥");
//		transitStepInfoList.add("2014-03-01 19:01:29快件已签收签收人拍照签收");
//		transitStepInfoList.add("2014-03-02 09:23:21快件到达杭州拱震桥上一站是杭州");
//		transitStepInfoList.add("2014-03-02 09:23:32杭州拱震桥的徐彦斌正在派件");
//	}

	public String getCompanyName() {
		return companyName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Long getTid() {
		return tid;
	}

	public void setTid(Long tid) {
		this.tid = tid;
	}

	public List<String> getTransitStepInfoList() {
		return transitStepInfoList;
	}

	public void setTransitStepInfoList(List<String> transitStepInfoList) {
		this.transitStepInfoList = transitStepInfoList;
	}

	public String getLogisticsStatus() {
		return logisticsStatus;
	}

	public void setLogisticsStatus(String logisticsStatus) {
		this.logisticsStatus = logisticsStatus;
	}

}
